package kz.codesmith.epay.loan.api.requirement.primitive;

import java.nio.charset.Charset;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.configuration.PkbConnectorProperties;
import kz.codesmith.epay.loan.api.model.pkb.OverduePayment;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@SpringRequirement
@RequiredArgsConstructor
public class OverdueRequirement implements Requirement<ScoringContext> {
  private final RestTemplate restTemplate;
  private final PkbConnectorProperties pkbConnectorProps;

  @Override
  @SneakyThrows
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    var iin = requestData.getIin();

    var isWhitelist = context.getRequestData().isWhiteList();
    log.info("OverduePaymentsCheck. Scoring context: {}", context.toString());
    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      context.getScoringInfo().setHasOverdue(false);
      return RequirementResult.success();
    }

    var overduePeriod = context.getVariablesHolder()
        .getValue(ScoringVars.OVERDUE_PERIOD, Long.class);
    var overdueAmount = context.getVariablesHolder()
        .getValue(ScoringVars.OVERDUE_AMOUNT, Long.class);

    var reportId = 6;
    var url = pkbConnectorProps.getUrl()
        + "/credit-report/{iin}/{reportId}/overdues?period={period}&amount={amount}";
    HttpEntity request = HttpEntity.EMPTY;

    var username = pkbConnectorProps.getUsername();
    var password = pkbConnectorProps.getPassword();
    if (StringUtils.isNotBlank(username)) {
      request = new HttpEntity(authHeaders(username, password));
    }

    OverduePayment [] overduePayments = null;
    try {
      var response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          request,
          OverduePayment[].class,
          iin,
          reportId,
          overduePeriod,
          overdueAmount
      );
      overduePayments = response.getBody();
    } catch (HttpClientErrorException e) {
      if (e.getRawStatusCode() == 404) {
        log.error("Failed to get Overdue Payments for {}; Due to missing report at PKB", iin);
        return RequirementResult.failure(RejectionReason.PREVIOUS_OVERDUES_CHECK_FAILED);
      } else {
        log.error("Failed to get Overdue Payments for {}; {}", iin, e.getMessage());
        return RequirementResult.failure(RejectionReason.SCORING_ERRORS);
      }
    } catch (HttpServerErrorException e) {
      log.error("Failed to get Overdue Payments for {}; {}", iin, e.getMessage());
      return RequirementResult.failure(RejectionReason.SCORING_ERRORS);
    }

    if (overduePayments != null && overduePayments.length > 0) {
      log.info(
          "Overdue payments found for iin={} in last {} months more than {} days",
          iin,
          overduePeriod,
          overdueAmount
      );
      context.getScoringInfo().setHasOverdue(true);
      return RequirementResult.failure(RejectionReason.PREVIOUS_OVERDUES);
    } else {
      log.info("No overdue payments found for iin={} in last {} months more than {} days",
          iin,
          overduePeriod,
          overdueAmount
      );
      context.getScoringInfo().setHasOverdue(false);
      return RequirementResult.success();
    }
  }

  private HttpHeaders authHeaders(String username, String password) {
    return new HttpHeaders() {
      {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    };
  }
}
