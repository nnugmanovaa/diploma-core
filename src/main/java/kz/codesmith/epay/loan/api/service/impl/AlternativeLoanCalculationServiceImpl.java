package kz.codesmith.epay.loan.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IAlternativeLoanCalculationService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.payintech.ListLoanMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlternativeLoanCalculationServiceImpl implements
    IAlternativeLoanCalculationService {

  private final IMfoCoreService mfoCoreService;

  @Value("${scoring.max-kdn}")
  private double maxKdn;

  @Override
  public List<AlternativeChoiceDto> calculateAlternative(ScoringContext context) {
    var loanType = context.getRequestData().getLoanMethod();
    var debt = context.getAlternativeLoanParams().getDebt();
    var income = context.getAlternativeLoanParams().getIncome();
    var alternativeAmountStep = context.getVariablesHolder()
        .getValue(ScoringVars.ALT_AMOUNT_STEP, BigDecimal.class);
    var alternativePeriodStep = context.getVariablesHolder()
        .getValue(ScoringVars.ALT_PERIOD_STEP, Integer.class);
    var minRate = context.getVariablesHolder().getValue(ScoringVars.MIN_RATE, Double.class);
    var maxGesv = context.getVariablesHolder().getValue(ScoringVars.MAX_GESV, Double.class);
    var clientInterestRate = (float) (minRate + context.getScoringInfo().getBadRate());
    var creditProduct = context.getRequestData().getLoanProduct();
    var maxLoanPeriod = context.getVariablesHolder()
        .getValue(ScoringVars.MAX_LOAN_PERIOD, Integer.class);


    var loanAmount = BigDecimal.valueOf(context.getRequestData().getLoanAmount());
    var loanMonthPeriod = context.getRequestData().getLoanPeriod();

    List<AlternativeChoiceDto> alternatives = new ArrayList<>();

    var newLoanAmount = loanAmount.subtract(alternativeAmountStep)
        .setScale(2, RoundingMode.HALF_UP);
    if (newLoanAmount.compareTo(BigDecimal.ZERO) < 1) {
      return null;
    }
    var alternativeAmount = calculateAlternativeAmount(
        debt,
        income,
        clientInterestRate,
        creditProduct,
        loanType,
        newLoanAmount,
        loanMonthPeriod,
        alternativeAmountStep,
        maxGesv
    );
    if (Objects.nonNull(alternativeAmount)) {
      log.info("Alternative amount calculated ({})", alternativeAmount);
      alternatives.add(AlternativeChoiceDto.builder()
          .loanAmount(alternativeAmount)
          .loanInterestRate(BigDecimal.valueOf(clientInterestRate))
          .loanMonthPeriod(loanMonthPeriod)
          .build());
    }

    var newLoanMonthPeriod = loanMonthPeriod + alternativePeriodStep;
    if (newLoanMonthPeriod > maxLoanPeriod) {
      return null;
    }
    var alternativePeriod = calculateAlternativePeriod(
        debt,
        income,
        clientInterestRate,
        creditProduct,
        loanType,
        loanAmount,
        newLoanMonthPeriod,
        alternativePeriodStep,
        maxGesv,
        maxLoanPeriod
    );
    if (Objects.nonNull(alternativePeriod)) {
      log.info("Alternative period calculated ({} months)", alternativePeriod);
      alternatives.add(AlternativeChoiceDto.builder()
          .loanAmount(loanAmount)
          .loanInterestRate(BigDecimal.valueOf(clientInterestRate))
          .loanMonthPeriod(alternativePeriod)
          .build());
    }
    log.info("{} alternatives calculated", alternatives.size());
    return alternatives;
  }

  @Override
  public List<AlternativeChoiceDto> calculateAlternative(BigDecimal loanAmount,
      Integer loanMonthPeriod, float clientInterestRate, String creditProduct,
      ListLoanMethod loanType, Double maxGesv, Integer orderId) {
    var loanSchedule = mfoCoreService.getLoanScheduleCalculation(
        loanAmount,
        loanMonthPeriod,
        clientInterestRate,
        creditProduct,
        loanType
    );
    if (Objects.nonNull(loanSchedule)) {
      log.info("New client effective rate for alternative={}, is={}",
          loanAmount, loanSchedule.getEffectiveRate());
      if (loanSchedule.getEffectiveRate() < maxGesv) {
        return Collections.singletonList(AlternativeChoiceDto.builder()
            .loanAmount(loanAmount)
            .loanInterestRate(BigDecimal.valueOf(clientInterestRate))
            .loanMonthPeriod(loanMonthPeriod)
            .loanEffectiveRate(loanSchedule.getEffectiveRate())
            .build());
      }
      log.info("New client effective rate is bigger than maxGesv, {}, {}",
          loanSchedule.getEffectiveRate(), maxGesv);
      return new ArrayList<>();
    } else {
      throw  new MfoGeneralApiException();
    }
  }

  private BigDecimal calculateAlternativeAmount(
      BigDecimal debt,
      BigDecimal income,
      float clientInterestRate,
      String creditProduct,
      ListLoanMethod loanType,
      BigDecimal loanAmount,
      Integer loanMonthPeriod,
      BigDecimal alternativeAmountStep,
      Double maxGesv
  ) {
    log.info("calculateAlternativeAmount("
        + "debt={}, income={}, clientInterestRate={}, "
        + "loanAmount={}, loanMonthPeriod={}, alternativeAmountStep={}, maxGesv={})",
        debt, income, clientInterestRate,
        loanAmount, loanMonthPeriod, alternativeAmountStep, maxGesv);

    var loanSchedule = mfoCoreService.getLoanScheduleCalculation(
        loanAmount,
        loanMonthPeriod,
        clientInterestRate,
        creditProduct,
        loanType
    );

    var avrMonthlyPayment = loanSchedule.getTotalAmountToBePaid()
        .divide(BigDecimal.valueOf(loanMonthPeriod), 2, RoundingMode.HALF_UP);

    var maxKdnAvrMonthlyPayment = BigDecimal.valueOf(maxKdn).multiply(income).subtract(debt)
        .setScale(2, RoundingMode.HALF_UP);

    if (avrMonthlyPayment.compareTo(maxKdnAvrMonthlyPayment) < 1) {

      if (loanSchedule.getEffectiveRate() < maxGesv) {
        return loanAmount;
      } else {
        return null;
      }

    } else {
      var newLoanAmount = loanAmount.subtract(alternativeAmountStep)
          .setScale(2, RoundingMode.HALF_UP);
      if (newLoanAmount.compareTo(BigDecimal.ZERO) < 1) {
        return null;
      }
      return calculateAlternativeAmount(
          debt,
          income,
          clientInterestRate,
          creditProduct,
          loanType,
          newLoanAmount,
          loanMonthPeriod,
          alternativeAmountStep,
          maxGesv
      );
    }
  }

  private Integer calculateAlternativePeriod(
      BigDecimal debt,
      BigDecimal income,
      float clientInterestRate,
      String creditProduct,
      ListLoanMethod loanType,
      BigDecimal loanAmount,
      Integer loanMonthPeriod,
      Integer alternativePeriodStep,
      Double maxGesv,
      Integer maxLoanPeriod
  ) {
    log.info("calculateAlternativePeriod("
            + "debt={}, income={}, clientInterestRate={}, "
            + "loanAmount={}, loanMonthPeriod={}, alternativePeriodStep={}, maxGesv={})",
        debt, income, clientInterestRate,
        loanAmount, loanMonthPeriod, alternativePeriodStep, maxGesv);

    var loanSchedule = mfoCoreService.getLoanScheduleCalculation(
        loanAmount,
        loanMonthPeriod,
        clientInterestRate,
        creditProduct,
        loanType
    );

    var avrMonthlyPayment = loanSchedule.getTotalAmountToBePaid()
        .divide(BigDecimal.valueOf(loanMonthPeriod), 2, RoundingMode.HALF_UP);

    var maxKdnAvrMonthlyPayment = BigDecimal.valueOf(maxKdn).multiply(income).subtract(debt)
        .setScale(2, RoundingMode.HALF_UP);

    if (avrMonthlyPayment.compareTo(maxKdnAvrMonthlyPayment) < 1) {

      if (loanSchedule.getEffectiveRate() < maxGesv) {
        return loanMonthPeriod;
      } else {
        return null;
      }

    } else {
      var newLoanMonthPeriod = loanMonthPeriod + alternativePeriodStep;
      if (newLoanMonthPeriod > maxLoanPeriod) {
        return null;
      }
      return calculateAlternativePeriod(
          debt,
          income,
          clientInterestRate,
          creditProduct,
          loanType,
          loanAmount,
          newLoanMonthPeriod,
          alternativePeriodStep,
          maxGesv,
          maxLoanPeriod
      );
    }
  }

}
