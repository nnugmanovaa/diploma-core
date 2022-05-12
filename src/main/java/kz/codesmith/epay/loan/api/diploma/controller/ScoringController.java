package kz.codesmith.epay.loan.api.diploma.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.diploma.model.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;
import kz.codesmith.epay.loan.api.diploma.service.IScoringService;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoringController {
  private final IScoringService scoringService;

  @ApiOperation(
      value = "Get Score result by ID.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/start-loan-process", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScoringResponse> score(@RequestBody ScoringRequest request) {
    return ResponseEntity.ok(scoringService.score(request));
  }

  @ApiOperation(
      value = "retrieves all active loans by iin",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/account-loans")
  public ResponseEntity<LoanCheckAccountResponse> getLoanInfo(
      @Valid @NotNull @RequestBody LoanCheckAccountRequestDto requestDto) {
    return ResponseEntity.ok(scoringService.getAccountLoans(requestDto));
  }

  @ApiOperation(
      value = "initializes payment",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/init-payment")
  public ResponseEntity<AcquiringPaymentResponse> initPayment(
      @Valid @NotNull @RequestBody LoanPaymentRequestDto requestDto) {
    AcquiringPaymentResponse paymentResponse = scoringService.initPayment(requestDto);
    return ResponseEntity.ok(paymentResponse);
  }
}
