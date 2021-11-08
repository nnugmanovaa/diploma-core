package kz.codesmith.epay.loan.api.service.impl;

import static kz.codesmith.epay.loan.api.model.calc.LoanVar.ANNUITY_METHOD_ON;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.DEFAULT_AMOUNT;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.DEFAULT_METHOD;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.DEFAULT_PERIOD;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.GRADED_METHOD_ON;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.LOAN_INTEREST;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.MAX_AMOUNT;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.MAX_PERIOD;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.MIN_AMOUNT;
import static kz.codesmith.epay.loan.api.model.calc.LoanVar.MIN_PERIOD;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kz.codesmith.epay.loan.api.model.calc.CalculatorInputs;
import kz.codesmith.epay.loan.api.model.calc.LoanPaymentMethod;
import kz.codesmith.epay.loan.api.repository.LoanVarsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanVariablesService {

  private final LoanVarsRepository loanVarsRepository;

  public Double getLoanInterest() {
    return Double.parseDouble("." + loanVarsRepository.findById(LOAN_INTEREST).get().getValue());
  }

  public CalculatorInputs getCalculatorInputs() {
    return CalculatorInputs.builder()
        .minAmount(getMinAmount())
        .maxAmount(getMaxAmount())
        .defaultAmount(getDefaultAmount())
        .minPeriod(getMinPeriod())
        .maxPeriod(getMaxPeriod())
        .defaultPeriod(getDefaultPeriod())
        .defaultMethod(getDefaultMethod())
        .methods(getPaymentMethods())
        .build();
  }

  public BigDecimal getMinAmount() {
    return new BigDecimal(loanVarsRepository.findById(MIN_AMOUNT).get().getValue());
  }

  public BigDecimal getMaxAmount() {
    return new BigDecimal(loanVarsRepository.findById(MAX_AMOUNT).get().getValue());
  }

  public BigDecimal getDefaultAmount() {
    return new BigDecimal(loanVarsRepository.findById(DEFAULT_AMOUNT).get().getValue());
  }

  public Integer getMinPeriod() {
    return Integer.parseInt(loanVarsRepository.findById(MIN_PERIOD).get().getValue());
  }

  public Integer getMaxPeriod() {
    return Integer.parseInt(loanVarsRepository.findById(MAX_PERIOD).get().getValue());
  }

  public Integer getDefaultPeriod() {
    return Integer.parseInt(loanVarsRepository.findById(DEFAULT_PERIOD).get().getValue());
  }

  public String getDefaultMethod() {
    return loanVarsRepository.findById(DEFAULT_METHOD).get().getValue();
  }

  public boolean isAnnuityOn() {
    var annuityOn = loanVarsRepository.findById(ANNUITY_METHOD_ON).get().getValue();
    return "true".equals(annuityOn) || "yes".equals(annuityOn);
  }

  public boolean isGradedOn() {
    var gradedOn = loanVarsRepository.findById(GRADED_METHOD_ON).get().getValue();
    return "true".equalsIgnoreCase(gradedOn) || "yes".equalsIgnoreCase(gradedOn);
  }

  public List<LoanPaymentMethod> getPaymentMethods() {
    var methods = new ArrayList<LoanPaymentMethod>();
    var defaultMethod = getDefaultMethod();

    if (isAnnuityOn()) {
      var annuity =
          LoanPaymentMethod.builder()
              .methodId("annuity")
              .methodNameRu("Аннуитетный")
              .methodNameKk("Аннуитет")
              .isDefault("annuity".equalsIgnoreCase(defaultMethod))
              .build();
      methods.add(annuity);
    }

    if (isGradedOn()) {
      var graded =
          LoanPaymentMethod.builder()
              .methodId("graded")
              .methodNameRu("Равными долями")
              .methodNameKk("Тең үлестер")
              .isDefault("graded".equalsIgnoreCase(defaultMethod))
              .build();
      methods.add(graded);
    }

    return methods;
  }
}
