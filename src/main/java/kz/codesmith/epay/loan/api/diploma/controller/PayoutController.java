package kz.codesmith.epay.loan.api.diploma.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.diploma.service.IPayoutService;
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
@RequestMapping("/payout")
@RequiredArgsConstructor
public class PayoutController {

  private final IPayoutService payoutService;

  @ApiOperation(
      value = "Init withdraw to card through acquiring",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/card", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PayoutToCardResponseDto> initPayoutToCard(
      @RequestBody @Valid PayoutToCardRequestDto request
  ) {
    return ResponseEntity.ok(payoutService.initPayoutToCard(request));
  }
}
