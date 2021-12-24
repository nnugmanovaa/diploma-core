package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.service.IPayoutService;
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

  @PreAuthorize("hasAuthority('CLIENT_USER')")
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

  @ApiOperation(
      value = "Payout callback URL",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/callback")
  public ResponseEntity<Void> acceptBankResponse(
      @RequestBody @NotBlank String body
  ) {

    payoutService.acceptPayoutCallbackRequest(body);
    return ResponseEntity.ok().build();
  }
}
