package kz.codesmith.epay.loan.api.controller;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.core.shared.views.Views;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.payment.ILoanPayment;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanDeatilsRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanDetailsResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Logged
@RestController
@RequestMapping("/loans")
@PreAuthorize("hasAuthority('CLIENT_USER')")
@RequiredArgsConstructor
public class LoanPaymentController {
  private final ILoanPayment loanPayment;
  private final IPaymentService paymentService;

  @PreAuthorize("hasAuthority('MFO_ADMIN')")
  @GetMapping("/loan-payments")
  public ResponseEntity<Page<LoanPaymentDto>> getLoanPaymentsByLoanOrderId(
      @RequestParam @NotNull Integer loanOrderId,
      @ApiIgnore Pageable pageable) {
    return ResponseEntity.ok(paymentService.getLoanPaymentsByLoanOrderId(pageable, loanOrderId));
  }

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
    AcquiringPaymentResponse paymentResponse = loanPayment.initPayment(requestDto);
    return ResponseEntity.ok(paymentResponse);
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

  @PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN', 'CLIENT_USER')")
  @ApiOperation(
      value = "retrieve loan payments",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @ApiPageable
  @GetMapping
  @JsonView(Views.Public.class)
  public ResponseEntity<Page<LoanPaymentDto>> getLoanPayments(
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate startDate,
      @RequestParam @NotNull @DateTimeFormat(iso = ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) List<MfoProcessingStatus> states,
      @ApiIgnore Pageable pageRequest
  ) {
    return ResponseEntity
        .ok(paymentService
            .getLoanPaymentsByOwner(startDate, endDate, states, pageRequest));
  }

  @ApiOperation(
      value = "retrieves loan details by iin and contract number",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/details")
  public ResponseEntity<LoanDetailsResponseDto> getLoanDetails(
      @Valid @NotNull @RequestBody LoanDeatilsRequestDto requestDto) {
    return ResponseEntity.ok(loanPayment.getLoanDetails(requestDto));
  }
}
