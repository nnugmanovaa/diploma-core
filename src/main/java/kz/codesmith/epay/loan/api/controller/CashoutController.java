package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.cashout.CardCashoutRequestDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardCashoutResponseDto;
import kz.codesmith.epay.loan.api.service.ICashoutService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/cashout")
@RequiredArgsConstructor
public class CashoutController {

  private final ICashoutService cashoutService;

  @PreAuthorize("hasAuthority('CLIENT_USER')")
  @ApiOperation(
      value = "Init withdraw to wallet by orderId",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/{id}/wallet", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> initCashoutToWallet(@PathVariable("id") @NotNull Integer orderId) {
    cashoutService.initCashoutToWallet(orderId);
    return ResponseEntity.ok().build();
  }

  @PreAuthorize("hasAuthority('CLIENT_USER')")
  @ApiOperation(
      value = "Init withdraw to card through halyk epay",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/halyk/epay/card", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<HalykCardCashoutResponseDto> initCashoutToCard(
      @RequestBody @Valid CardCashoutRequestDto request
  ) {
    return ResponseEntity.ok(cashoutService.initHalykEpayCashoutToCard(request));
  }

}
