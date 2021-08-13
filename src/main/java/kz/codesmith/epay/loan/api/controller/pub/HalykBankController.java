package kz.codesmith.epay.loan.api.controller.pub;

import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.loan.api.service.ICashoutService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/halyk")
@RequiredArgsConstructor
public class HalykBankController {

  private final ICashoutService cashoutService;

  @ApiOperation(
      value = "Halykbank callback URL",
      produces = MediaType.TEXT_PLAIN_VALUE
  )
  @PostMapping(path = "/callback", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> acceptBankResponse(
      @RequestParam("response") @NotBlank String requestXml
  ) {
    cashoutService.acceptHalykBankCallbackRequest(requestXml);
    return ResponseEntity.ok("00");
  }

}
