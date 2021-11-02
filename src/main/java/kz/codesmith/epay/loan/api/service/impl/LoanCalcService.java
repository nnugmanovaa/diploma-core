package kz.codesmith.epay.loan.api.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import kz.codesmith.epay.loan.api.exception.LoanAmountException;
import kz.codesmith.epay.loan.api.exception.LoanPeriodException;
import kz.codesmith.epay.loan.api.model.calc.AnnuityMonthlyPayment;
import kz.codesmith.epay.loan.api.model.calc.GradedMonthlyPayments;
import kz.codesmith.epay.loan.api.model.calc.MonthlyPayment;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanCalcService {

  private final LoanVariablesService loanVars;
  private final MessageSource messageSource;

  public AnnuityMonthlyPayment annuityMethod(BigDecimal amount, Integer period) {
    var monthlyInterestRate = loanVars.getLoanInterest() / 12;

    validateInputs(amount, period);

    var monthlyPayment = amount.multiply(new BigDecimal((
        (monthlyInterestRate * (Math.pow(1 + monthlyInterestRate, period)))
            / ((Math.pow(1 + monthlyInterestRate, period)) - 1)
    )));
    AnnuityMonthlyPayment annuityMonthlyPayment = new AnnuityMonthlyPayment();
    annuityMonthlyPayment.setMonthlyPayment(monthlyPayment.setScale(0, RoundingMode.HALF_UP));

    return annuityMonthlyPayment;
  }

  private void validateInputs(BigDecimal amount, Integer period) {
    var minAmount = loanVars.getMinAmount();
    var maxAmount = loanVars.getMaxAmount();
    var minPeriod = loanVars.getMinPeriod();
    var maxPeriod = loanVars.getMaxPeriod();

    var isLargerThanMinAmount = amount.compareTo(minAmount) >= 0;
    var isLessThanMaxAmount = amount.compareTo(maxAmount) <= 0;

    if (!isLargerThanMinAmount || !isLessThanMaxAmount) {
      var errMsg = messageSource.getMessage(
          "loan.calc.amount.error",
          new Object[]{minAmount, maxAmount},
          LocaleContextHolder.getLocale()
      );
      throw new LoanAmountException(errMsg, minAmount, maxAmount);
    }

    if (period < minPeriod || period > maxPeriod) {
      var errMsg = messageSource.getMessage(
          "loan.calc.period.error",
          new Object[]{minPeriod, maxPeriod},
          LocaleContextHolder.getLocale()
      );
      throw new LoanPeriodException(errMsg, minPeriod, maxPeriod);
    }
  }

  public List<MonthlyPayment> gradedMethod(BigDecimal amount, int period) {
    validateInputs(amount, period);

    GradedMonthlyPayments gradedMonthlyPayments = new GradedMonthlyPayments();
    BigDecimal monthlyInterestRate = new BigDecimal(loanVars.getLoanInterest() / 12);
    BigDecimal monthPrincipal = amount.divide(new BigDecimal(period), 9, RoundingMode.HALF_UP);
    BigDecimal remainTotal = amount;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");

    ArrayList<MonthlyPayment> loanPayment = new ArrayList<>();

    for (int i = 1; i <= period; i++) {
      BigDecimal interest = remainTotal.multiply(monthlyInterestRate)
          .setScale(2, RoundingMode.HALF_UP);

      remainTotal = remainTotal.subtract(monthPrincipal);
      calendar.add(Calendar.MONTH, 1);

      BigDecimal monthPayTotal = monthPrincipal.add(interest);
      MonthlyPayment monthlyPayment = new MonthlyPayment();
      monthlyPayment.setOrder(i);
      monthlyPayment.setAmount(monthPayTotal.setScale(0, RoundingMode.HALF_UP));
      monthlyPayment.setYear(Integer.parseInt(simpleDateFormat.format(calendar.getTime())));
      monthlyPayment = getMonth(calendar.get(Calendar.MONTH) + 1, monthlyPayment);
      loanPayment.add(monthlyPayment);
    }

    gradedMonthlyPayments.setMonthlyPayments(loanPayment);
    return gradedMonthlyPayments.getMonthlyPayments();
  }

  private MonthlyPayment getMonth(Integer indexMonth, MonthlyPayment monthlyPayment) {
    switch (indexMonth) {
      case (1): {
        monthlyPayment.setMonth((short) 1);
        monthlyPayment.setMonthRu("Январь");
        monthlyPayment.setMonthKk("Қаңтар");
        break;
      }

      case (2): {
        monthlyPayment.setMonth((short) 2);
        monthlyPayment.setMonthRu("Февраль");
        monthlyPayment.setMonthKk("Ақпан");
        break;
      }

      case (3): {
        monthlyPayment.setMonth((short) 3);
        monthlyPayment.setMonthRu("Март");
        monthlyPayment.setMonthKk("Наурыз");
        break;
      }

      case (4): {
        monthlyPayment.setMonth((short) 4);
        monthlyPayment.setMonthRu("Апрель");
        monthlyPayment.setMonthKk("Сәуір");
        break;
      }

      case (5): {
        monthlyPayment.setMonth((short) 5);
        monthlyPayment.setMonthRu("Май");
        monthlyPayment.setMonthKk("Мамыр");
        break;
      }

      case (6): {
        monthlyPayment.setMonth((short) 6);
        monthlyPayment.setMonthRu("Июнь");
        monthlyPayment.setMonthKk("Маусым");
        break;
      }

      case (7): {
        monthlyPayment.setMonth((short) 7);
        monthlyPayment.setMonthRu("Июль");
        monthlyPayment.setMonthKk("Шілде");
        break;
      }

      case (8): {
        monthlyPayment.setMonth((short) 8);
        monthlyPayment.setMonthRu("Август");
        monthlyPayment.setMonthKk("Тамыз");
        break;
      }

      case (9): {
        monthlyPayment.setMonth((short) 9);
        monthlyPayment.setMonthRu("Сентябрь");
        monthlyPayment.setMonthKk("Қыркүйек");
        break;
      }

      case (10): {
        monthlyPayment.setMonth((short) 10);
        monthlyPayment.setMonthRu("Октябрь");
        monthlyPayment.setMonthKk("Қазан");
        break;
      }

      case (11): {
        monthlyPayment.setMonth((short) 11);
        monthlyPayment.setMonthRu("Ноябрь");
        monthlyPayment.setMonthKk("Қараша");
        break;
      }

      case (12): {
        monthlyPayment.setMonth((short) 12);
        monthlyPayment.setMonthRu("Декабрь");
        monthlyPayment.setMonthKk("Желтоқсан");
        break;
      }
      default:
        break;
    }
    return monthlyPayment;
  }

}
