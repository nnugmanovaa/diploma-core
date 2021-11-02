package kz.codesmith.epay.loan.api.controller;

import javax.validation.Valid;
import kz.codesmith.epay.loan.api.model.PhoneVerificationCheck;
import kz.codesmith.epay.loan.api.model.PhoneVerificationRequest;
import kz.codesmith.epay.loan.api.service.impl.PhoneVerificationService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequiredArgsConstructor
@RequestMapping("/phone-verification")
public class PhoneVerificationController {

  private final PhoneVerificationService verificationService;

  @PostMapping
  public void sendVerificationCode(
      @Valid @RequestBody PhoneVerificationRequest verificationRequest
  ) {
    log.info("Verification code requested by {}", verificationRequest.getMsisdn());
    verificationService.generateAndSendVerificationCode(verificationRequest);
  }

  @PostMapping("/check")
  public void validateCode(
      @Valid @RequestBody PhoneVerificationCheck verificationCheck
  ) {
    log.info("Validate verification code for {}", verificationCheck.getMsisdn());
    verificationService.validateVerificationCode(verificationCheck);
  }
}
