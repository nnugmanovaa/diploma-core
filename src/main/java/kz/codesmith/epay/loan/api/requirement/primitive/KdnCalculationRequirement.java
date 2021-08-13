package kz.codesmith.epay.loan.api.requirement.primitive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeLoanParams;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeRejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;


@SpringRequirement
@RequiredArgsConstructor
@Slf4j
public class KdnCalculationRequirement implements Requirement<ScoringContext> {

  private final IPkbScoreService pkbScoreService;

  @Value("${scoring.max-kdn}")
  private double maxKdn;

  @Override
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    var iin = requestData.getIin();
    var isWhitelist = context.getRequestData().isWhiteList();

    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }

    var report = pkbScoreService.getKdnScore(
        requestData.getIin(),
        requestData.getPersonalInfo().getBirthDate(),
        true,
        requestData.getPersonalInfo().getFirstName(),
        requestData.getPersonalInfo().getLastName(),
        requestData.getPersonalInfo().getMiddleName()
    );

    if (report.getKdnScore() == null || report.getDebt() == null || report.getIncome() == null) {
      return RequirementResult.failure(RejectionReason.KDN_INCOME_OR_DEBT_UNAVAILABLE);
    }

    var kdnScore = Double.parseDouble(report.getKdnScore());
    log.info("PKB KDN is {}", kdnScore);
    if (kdnScore >= maxKdn) {
      return RequirementResult.failure(RejectionReason.KDN_TOO_BIG);
    }

    var avgMonthlyPayment = context.getLoanSchedule().getTotalAmountToBePaid();
    var debt = new BigDecimal(report.getDebt());
    var income = new BigDecimal(report.getIncome());
    var newKdn = avgMonthlyPayment.add(debt).divide(income, 2, RoundingMode.HALF_UP);

    log.info("New KDN is {}", newKdn.toString());

    if (newKdn.doubleValue() > maxKdn) {
      context.setAlternativeLoanParams(new AlternativeLoanParams(income, debt));
      return RequirementResult.failure(AlternativeRejectionReason.NEW_KDN_TOO_BIG);
    }

    context.getScoringInfo().setKdn(kdnScore);
    context.getScoringInfo().setNewKdn(newKdn);
    context.getScoringInfo().setDebt(debt);
    context.getScoringInfo().setIncome(income);
    return RequirementResult.success();
  }

}
