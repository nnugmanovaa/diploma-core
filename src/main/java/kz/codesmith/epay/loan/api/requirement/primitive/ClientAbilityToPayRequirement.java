package kz.codesmith.epay.loan.api.requirement.primitive;

import com.fcb.closedcontracts.service.web.ServiceReturn;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

@SpringRequirement
@RequiredArgsConstructor
@Slf4j
public class ClientAbilityToPayRequirement implements Requirement<ScoringContext> {

  private final IPkbScoreService pkbScoreService;
  private double costOfLiving;

  @Value("${scoring.max-kdn}")
  private double maxKdn;

  @Override
  public RequirementResult check(ScoringContext context) {
    double dz;
    var requestData = context.getRequestData();
    var iin = requestData.getIin();
    var isWhitelist = context.getRequestData().isWhiteList();

    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }

    costOfLiving = context.getVariablesHolder().getValue(ScoringVars.COST_OF_LIVING, Double.class);

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
      //TODO call pksService.getKdnScore & use deductionsDetailed field from report to calculate dz
      dz = 10000D;
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

}
