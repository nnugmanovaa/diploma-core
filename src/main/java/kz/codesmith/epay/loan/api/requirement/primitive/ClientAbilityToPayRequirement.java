package kz.codesmith.epay.loan.api.requirement.primitive;

import com.fcb.closedcontracts.service.web.ServiceReturn;
import java.nio.charset.Charset;
import java.util.List;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.configuration.PkbConnectorProperties;
import kz.codesmith.epay.loan.api.model.pkb.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.model.pkb.kdn.DeductionsDetailed;
import kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
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
public class ClientAbilityToPayRequirement implements Requirement<ScoringContext> {

  private static final int DEDUCTION_AMOUNTS_COUNT_THRESHOLD = 2;
  private static final String KDN_SUCCESS_RESP_CODE = "FOUND";

  private final IPkbScoreService pkbScoreService;
  private double costOfLiving;

  private final PkbConnectorProperties pkbConnectorProps;
  private final RestTemplate restTemplate;
  private final IScoreVariablesService scoreVarService;

  @Value("${scoring.max-kdn}")
  private double maxKdn;

  @Override
  public RequirementResult check(ScoringContext context) {
    double dz;
    var requestData = context.getRequestData();
    var iin = requestData.getIin();
    var isWhitelist = context.getRequestData().isWhiteList();
    var isBlacklist = context.getRequestData().isBlackList();

    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }

    if (isBlacklist) {
      log.info("IIN in blackList {}, no scoring", iin);
      return RequirementResult.success();
    }

    costOfLiving = scoreVarService.getValue(ScoringVars.COST_OF_LIVING, Double.class);

    //it seems response never returned as null
    ServiceReturn response = pkbScoreService.getContractSum(requestData.getIin(), true);

    String maxContractSum = response.getMaxContractSum();
    String maxPaymentSum = response.getMaxPaymentSum();

    if (StringUtils.isNotBlank(maxContractSum) && StringUtils.isNotBlank(maxPaymentSum)) {
      dz = getBiggerOne(maxContractSum, maxPaymentSum);
      context.getScoringInfo().setMaxPaymentSum(Double.parseDouble(maxPaymentSum));
      context.getScoringInfo().setMaxContractSum(Double.parseDouble(maxContractSum));
    } else if (StringUtils.isNotBlank(maxContractSum)) {
      dz = Double.parseDouble(maxContractSum);
    } else if (StringUtils.isNotBlank(maxPaymentSum)) {
      dz = Double.parseDouble(maxPaymentSum);
    } else {
      log.info("No maxPaymentSum or maxContractSum found. Going to check deductions from KDN");
      try {
        dz = incomeCheckWithoutCreditHistory(requestData);
      } catch (Exception e) {
        log.error("Error during deductions check for {} from KDN data", iin, e);
        return RequirementResult.failure(RejectionReason.NO_PREVIOUS_HIST_AND_DEDUCTIONS);
      }

    }

    var clientCostOfLiving = calculateClientCostOfLiving(
        requestData.getPersonalInfo().getNumberOfKids()
    );
    if (clientCostOfLiving > dz) {
      return RequirementResult.failure(RejectionReason.NO_ABILITY_TO_PAY);
    }
    return RequirementResult.success();
  }

  private double getBiggerOne(String str1, String str2) {
    var val1 = Double.parseDouble(str1);
    var val2 = Double.parseDouble(str2);

    return Math.max(val1, val2);
  }

  private double calculateClientCostOfLiving(Integer numberOfKids) {
    return maxKdn * (costOfLiving * numberOfKids) + costOfLiving;
  }

  private double incomeCheckWithoutCreditHistory(
      ScoringRequest requestData
  ) {

    var req = KdnRequest.builder()
        .iin(requestData.getIin())
        .birthdate(requestData.getPersonalInfo().getBirthDate())
        .firstname(requestData.getPersonalInfo().getFirstName())
        .lastname(requestData.getPersonalInfo().getLastName())
        .middlename(requestData.getPersonalInfo().getMiddleName())
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
      var kdnAppReport = response.getBody();
      var kdnData = kdnAppReport.getIncomesResultCrtrV2()
          .getResult().getResponseData().getData();

      if (KDN_SUCCESS_RESP_CODE.equalsIgnoreCase(kdnData.getResponseCode())) {
        if (kdnData.getDeductionsDetailed().size() >= DEDUCTION_AMOUNTS_COUNT_THRESHOLD) {
          var deductionsAmountsAvg = calcAverage(kdnData.getDeductionsDetailed());
          log.info("Deductions amounts average = {}", deductionsAmountsAvg);
          return deductionsAmountsAvg;
        }
      }
    } else {
      log.error("PKBConnector returned {} code", response.getStatusCode().value());
    }

    return -1;
  }

  private double calcAverage(List<DeductionsDetailed> deductionsDetailed) {
    var sum = deductionsDetailed.stream()
        .map(DeductionsDetailed::getAmount)
        .reduce(0.0, Double::sum);
    return sum / deductionsDetailed.size();
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
