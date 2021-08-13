package kz.codesmith.epay.loan.api.payment;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/loans")
@PreAuthorize("hasAuthority('CLIENT_USER')")
@RequiredArgsConstructor
public class LoanPaymentController {
  private final ILoanPayment loanPayment;

  @ApiOperation(
      value = "retrieves all active loans by iin",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/account-loans")
  public ResponseEntity<LoanCheckAccountResponse> getLoanInfo(
      @Valid @NotNull @RequestBody LoanCheckAccountRequestDto requestDto) {
    return ResponseEntity.ok(loanPayment.getAccountLoans(requestDto));
  }

  @ApiOperation(
      value = "initializes payment",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/init-payment")
  public ResponseEntity<AcquiringPaymentResponse> initPayment(
      @Valid @NotNull @RequestBody LoanPaymentRequestDto requestDto) {
    return ResponseEntity.ok(loanPayment.initPayment(requestDto));
  }

  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN')")
  @ApiOperation(
      value = "repayment method",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping("/retry-payment")
  public ResponseEntity<LoanPaymentResponseDto> retryPayment(
      @RequestParam(name = "paymentId") @NotNull Integer paymentId) {
    return ResponseEntity.ok(loanPayment.retryPayment(paymentId));
  }
}
