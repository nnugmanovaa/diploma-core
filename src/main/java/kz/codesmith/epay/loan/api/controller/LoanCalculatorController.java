package kz.codesmith.epay.loan.api.controller;

import java.math.BigDecimal;
import kz.codesmith.epay.loan.api.service.impl.LoanCalcService;
import kz.codesmith.epay.loan.api.service.impl.LoanVariablesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/calculator")
public class LoanCalculatorController {

  private final LoanCalcService loanCalcService;
  private final LoanVariablesService loanVarsService;

  @GetMapping("/inputs")
  public ResponseEntity<?> getCalculatorInputs() {
    var monthlyPayment = loanVarsService.getCalculatorInputs();
    return ResponseEntity.ok(monthlyPayment);
  }

  @GetMapping("/annuity")
  public ResponseEntity<?> calcAnnuity(
      @RequestParam BigDecimal amount,
      @RequestParam Integer period
  ) {
    var monthlyPayment = loanCalcService.annuityMethod(amount, period);
    return ResponseEntity.ok(monthlyPayment);
  }

  @GetMapping("/graded")
  public ResponseEntity<?> calcGraded(
      @RequestParam BigDecimal amount,
      @RequestParam Integer period
  ) {
    var monthlyPayments = loanCalcService.gradedMethod(amount, period);
    return ResponseEntity.ok(monthlyPayments);
  }
}
