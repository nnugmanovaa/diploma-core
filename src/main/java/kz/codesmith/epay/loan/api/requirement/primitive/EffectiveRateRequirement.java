package kz.codesmith.epay.loan.api.requirement.primitive;

import java.math.BigDecimal;
import java.util.List;
import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.Requirement;
import kz.codesmith.epay.loan.api.requirement.RequirementResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
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
  private final ILoanOrdersService loanOrdersService;
  private final IScoreVariablesService scoreVarService;


  @Override
  @SneakyThrows
  public RequirementResult check(ScoringContext context) {
    var requestData = context.getRequestData();
    var iin = requestData.getIin();
    var minRate = context.getVariablesHolder().getValue(ScoringVars.MIN_RATE, Double.class);
    var minScoreBall = scoreVarService.getValue(ScoringVars.MIN_SCORE_BALL, Integer.class);
    var maxInterestRate = scoreVarService
        .getValue(ScoringVars.MAX_INTRST_RATE, Integer.class);
    var loanInterestRate = (float) (minRate + context.getScoringInfo().getBadRate());

    if (loanInterestRate > maxInterestRate
        && context.getScoringInfo().getScore() > minScoreBall) {
      List<OrderDto> orderDtos = loanOrdersService
          .findAllOpenLoansByIin(context.getRequestData().getIin());
      if (orderDtos.size() == 0) {
        log.info("Loan interest rate set from {} to 45, for client={}", loanInterestRate,
            context.getRequestData().getIin());
        loanInterestRate = maxInterestRate;
      }
    }

    LoanSchedule loanSchedule = mfoCoreService.getLoanScheduleCalculation(
        BigDecimal.valueOf(requestData.getLoanAmount()),
        requestData.getLoanPeriod(),
        loanInterestRate,
        requestData.getLoanProduct(),
        requestData.getLoanMethod());

    context.setLoanSchedule(loanSchedule);
    context.setInterestRate(BigDecimal.valueOf(loanInterestRate));

    var isWhitelist = context.getRequestData().isWhiteList();
    if (isWhitelist) {
      log.info("IIN in whitelist {}, no scoring", iin);
      return RequirementResult.success();
    }

    var maxGesv = context.getVariablesHolder().getValue(ScoringVars.MAX_GESV, Double.class);

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
