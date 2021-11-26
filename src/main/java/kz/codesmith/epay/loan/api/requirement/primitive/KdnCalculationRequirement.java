package kz.codesmith.epay.loan.api.requirement.primitive;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.configuration.PkbConnectorProperties;
import kz.codesmith.epay.loan.api.model.exception.PkbRequestFailedException;
import kz.codesmith.epay.loan.api.model.pkb.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.model.pkb.kdn.Data;
import kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeLoanParams;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeRejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;


@Slf4j
@SpringRequirement
@RequiredArgsConstructor
public class KdnCalculationRequirement implements Requirement<ScoringContext> {

  private final PkbConnectorProperties pkbConnectorProps;
  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final IScoreVariablesService scoreVarsService;

  @Value("${scoring.max-kdn}")
  private double maxKdn;

  @Override
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    var iin = requestData.getIin();
    var isWhitelist = context.getRequestData().isWhiteList();
    var isBlackList = context.getRequestData().isBlackList();
    var minScoreBall = scoreVarsService.getValue(ScoringVars.MIN_SCORE_BALL, Integer.class);
    var maxKdnAlter = scoreVarsService.getValue(ScoringVars.MAX_KDN_ALTER, Integer.class);
    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }
    ApplicationReport report;
    if (isBlackList) {
      report = getFakeKdnReportData(requestData.getIin());
    } else {
      report = getKdnScore(
          requestData.getIin(),
          requestData.getPersonalInfo().getBirthDate(),
          requestData.getPersonalInfo().getFirstName(),
          requestData.getPersonalInfo().getLastName(),
          requestData.getPersonalInfo().getMiddleName()
      );
    }

    if (Objects.nonNull(report.getErrorCode()) && "4".equals(report.getErrorCode())) {
      log.info("PKB kdn response {}", report.getErrorMessage());
      return RequirementResult.failure(RejectionReason.KDN_INCOME_OR_DEBT_UNAVAILABLE);
    }

    if (report.getKdnScore() == null || report.getDebt() == null || report.getIncome() == null) {
      return RequirementResult.failure(RejectionReason.KDN_INCOME_OR_DEBT_UNAVAILABLE);
    }

    log.info("KDN score result income:{}, kdn ={}, debt={}", report.getIncome(),
        report.getKdnScore(),
        report.getDebt());

    var kdnScore = report.getKdnScore();
    var debt = new BigDecimal(report.getDebt());
    var income = new BigDecimal(report.getIncome());

    context.getScoringInfo().setKdn(kdnScore);
    context.getScoringInfo().setDebt(debt);
    context.getScoringInfo().setIncome(income);

    log.info("PKB KDN is {}", kdnScore);
    if (kdnScore >= maxKdn) {
      log.info("(kdnScore >= maxKdn) {} >= {}. check for alternative",
          kdnScore, maxKdn);
      Integer userScore = context.getScoringInfo().getScore();
      if (userScore > minScoreBall && kdnScore <= maxKdnAlter) {
        log.info("(kdnScore >= maxKdn) {} >= {} and (kdnScore <= maxKdnForAlternative) {} <= {}",
            kdnScore, maxKdn, kdnScore, maxKdnAlter);
        Double loanAmount = context.getRequestData().getLoanAmount();
        Double costOfLiving = context.getVariablesHolder()
            .getValue(ScoringVars.COST_OF_LIVING, Double.class);
        if (loanAmount > costOfLiving) {
          context.setMaxLoanAmount(BigDecimal.valueOf(costOfLiving));
          return RequirementResult
              .failure(AlternativeRejectionReason.KDN_TOO_BIG_SUGGEST_ALTERNATIVE);
        }
        return RequirementResult.success();
      }
      return RequirementResult.failure(RejectionReason.KDN_TOO_BIG);
    }

    var period = new BigDecimal(requestData.getLoanPeriod());
    var avgMonthlyPayment = context.getLoanSchedule().getTotalAmountToBePaid()
        .divide(period, 2, RoundingMode.HALF_UP);
    var newKdn = avgMonthlyPayment.add(debt).divide(income, 2, RoundingMode.HALF_UP);

    log.info("New KDN is {}", newKdn.toString());

    if (newKdn.doubleValue() > maxKdn) {
      log.info("(newKdn > maxKdn) {} > {}. return AlternativeRejectionReason.NEW_KDN_TOO_BIG",
          newKdn, maxKdn);
      context.setAlternativeLoanParams(new AlternativeLoanParams(income, debt));
      return RequirementResult.failure(AlternativeRejectionReason.NEW_KDN_TOO_BIG);
    }

    Data deductionsData;
    if (report != null
        && report.getIncomesResultCrtrV2() != null
        && report.getIncomesResultCrtrV2().getResult() != null
        && report.getIncomesResultCrtrV2().getResult().getResponseData() != null) {

      deductionsData = report.getIncomesResultCrtrV2()
          .getResult()
          .getResponseData()
          .getData();
      context.getScoringInfo().setIncomesInfo(
          objectMapper.convertValue(deductionsData, Map.class));
    }

    context.getScoringInfo().setKdn(kdnScore);
    context.getScoringInfo().setNewKdn(newKdn);

    log.info("KdnCalculationRequirement.success()");
    return RequirementResult.success();
  }

  private ApplicationReport getKdnScore(
      String iin,
      LocalDate birthDay,
      String firstName,
      String lastName,
      String middleName
  ) {
    var req = KdnRequest.builder()
        .iin(iin)
        .birthdate(birthDay)
        .firstname(firstName)
        .lastname(lastName)
        .middlename(middleName)
        .build();

    var url = pkbConnectorProps.getUrl() + "/kdn";
    var requestEntity = HttpEntity.EMPTY;

    var username = pkbConnectorProps.getUsername();
    var password = pkbConnectorProps.getPassword();

    if (StringUtils.isNotBlank(username)) {
      requestEntity = new HttpEntity(req, authHeaders(username, password));
    } else {
      requestEntity = new HttpEntity<KdnRequest>(req);
    }

    log.info("HTTP PUT {} payload: {}", url, req);
    var response = restTemplate.exchange(
        url,
        HttpMethod.PUT,
        requestEntity,
        ApplicationReport.class
    );

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      log.error("PKBConnector returned {} code", response.getStatusCode().value());
      throw new PkbRequestFailedException("Failed to get KDN score");
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

  private ApplicationReport getFakeKdnReportData(String iin) {
    ApplicationReport report = new ApplicationReport();
    report.setDebt(0d);
    report.setIncome(200000d);
    report.setIin(iin);
    report.setKdnScore(0.8);
    return report;
  }
}
