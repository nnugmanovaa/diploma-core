package kz.codesmith.epay.loan.api.requirement.primitive;

import java.math.BigDecimal;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.impl.MfoCoreService;
import kz.payintech.LoanSchedule;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringRequirement
@RequiredArgsConstructor
@Slf4j
public class EffectiveRateRequirement implements Requirement<ScoringContext> {

  private final MfoCoreService mfoCoreService;

  @Override
  @SneakyThrows
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    var minRate = context.getVariablesHolder().getValue(ScoringVars.MIN_RATE, Double.class);
    var maxGesv = context.getVariablesHolder().getValue(ScoringVars.MAX_GESV, Double.class);
    var loanInterestRate = (float) (minRate + context.getScoringInfo().getBadRate());
    var iin = requestData.getIin();
    var isWhitelist = context.getRequestData().isWhiteList();

    LoanSchedule loanSchedule = mfoCoreService.getLoanScheduleCalculation(
        BigDecimal.valueOf(requestData.getLoanAmount()),
        requestData.getLoanPeriod(),
        loanInterestRate,
        requestData.getLoanProduct(),
        requestData.getLoanMethod());

    context.setLoanSchedule(loanSchedule);

    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }

    log.info(
        "Effective Rate Check [loanEffectiveRate={} maxGesv={}]",
        loanSchedule.getEffectiveRate(),
        maxGesv
    );
    if (loanSchedule.getEffectiveRate() > maxGesv) {
      return RequirementResult.failure(RejectionReason.BAD_EFFECTIVE_RATE);
    }

    return RequirementResult.success();
  }

}
