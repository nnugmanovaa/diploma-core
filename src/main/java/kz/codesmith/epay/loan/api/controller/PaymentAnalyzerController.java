package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.service.IPaymentAnalyzerService;
import kz.codesmith.epay.loan.api.service.IPayoutService;
import kz.codesmith.epay.telegram.gw.controller.ITelegramBotController;
import kz.codesmith.epay.telegram.gw.models.dto.LoanPaymentAnalyzerDto;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/system/mfo")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('MFO_ADMIN', 'ADMIN')")
@Validated
public class PaymentAnalyzerController {

  private final ITelegramBotController telegramBotController;
  private final IPaymentAnalyzerService paymentAnalyzerService;
  private final IPayoutService payoutService;

  @ApiOperation(
      value = "retrieves all unprocessed payment within a specified period",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping("/payments")
  public ResponseEntity<?> getPayments(
      @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) @NotNull LocalDateTime startTime,
      @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) @NotNull LocalDateTime endTime
  ) {
    LoanPaymentAnalyzerDto loanPaymentAnalyzerDto = paymentAnalyzerService.analyzePaymentsBetween(
        startTime, endTime);
    if (!CollectionUtils.isEmpty(loanPaymentAnalyzerDto.getPayments())) {
      telegramBotController.sendLoanErrorPaymentsBetweenTime(loanPaymentAnalyzerDto);
    }
    return ResponseEntity.ok(loanPaymentAnalyzerDto);
  }

  @ApiOperation(
      value = "Check payout status"
  )
  @PostMapping(path = "/payout/status")
  public ResponseEntity<Void> checkPayoutStatuses() {
    payoutService.checkPayoutStatuses();
    return ResponseEntity.ok().build();
  }
}
