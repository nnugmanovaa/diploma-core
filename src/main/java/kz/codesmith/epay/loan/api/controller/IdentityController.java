package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.impl.IdentityMatchService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequiredArgsConstructor
@RequestMapping("/identity")
public class IdentityController {
  private final IdentityMatchService identityService;
  private final ILoanOrdersService loanOrdersService;

  @PostMapping("/{iin}/{orderId}")
  @ApiOperation("Match user identity")
  public ResponseEntity<Void> matchUserIdentity(
      @NotNull @PathVariable String iin,
      @NotNull @PathVariable String orderId,
      @RequestBody String base64Image
  ) {
    var result = identityService.matchUserIdentity(iin, orderId, base64Image);
    var intOrderId = Integer.parseInt(orderId);
    loanOrdersService.updateLoanOrderIdentityMatchResult(intOrderId, result.getResult());
    return ResponseEntity.ok().build();
  }
}
