package kz.codesmith.epay.loan.api.controller.pub;

import io.swagger.annotations.ApiOperation;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationRequest;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationResponse;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/acquiring")
@RequiredArgsConstructor
public class AcquiringController {

  private final IAcquiringService acquiringService;

  @ApiOperation(
      value = "Acquiring PostLink URL",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/callback", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AcquiringNotificationResponse> acceptBankResponse(
      @RequestBody @NotBlank String body
  ) {
    AcquiringNotificationRequest request = AcquiringNotificationRequest.builder()
        .body(body)
        .build();
    return ResponseEntity.ok(acquiringService.processNotification(request));
  }
}
