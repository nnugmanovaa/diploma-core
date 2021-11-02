package kz.codesmith.epay.loan.api.service.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import kz.codesmith.epay.loan.api.component.codegen.CodeGenerator;
import kz.codesmith.epay.loan.api.component.sms.SmsSender;
import kz.codesmith.epay.loan.api.domain.PhoneVerificationEntity;
import kz.codesmith.epay.loan.api.exception.TooEarlyForRequestException;
import kz.codesmith.epay.loan.api.exception.VerificationCodeExpiredException;
import kz.codesmith.epay.loan.api.exception.VerificationRequestNotFoundException;
import kz.codesmith.epay.loan.api.exception.WrongVerificationCodeException;
import kz.codesmith.epay.loan.api.model.PhoneVerificationCheck;
import kz.codesmith.epay.loan.api.model.PhoneVerificationRequest;
import kz.codesmith.epay.loan.api.repository.PhoneVerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhoneVerificationService {

  @Value("${app.phone-verification.wait-period-sec:60}")
  private Integer waitPeriodInSec; // wait time between requests

  @Value("${app.phone-verification.validity-period-min:60}")
  private long validityPeriodInMin; // code is valid for

  private final PhoneVerificationRepository verificationRepo;
  private final SmsSender smsSender;
  private final MessageSource messageSource;
  private final CodeGenerator codeGenerator;

  public void generateAndSendVerificationCode(PhoneVerificationRequest verificationRequest) {
    var msisdn = verificationRequest.getMsisdn();

    log.info("Phone verification for {} requested", msisdn);
    var verificationEntity = verificationRepo.getByMsisdn(msisdn);

    if (verificationEntity == null) {
      var code = codeGenerator.generateCode();

      var newVerificationRequest = new PhoneVerificationEntity();
      newVerificationRequest.setMsisdn(msisdn);
      newVerificationRequest.setCode(code);
      newVerificationRequest.setLastRequestDate(LocalDateTime.now());
      verificationRepo.saveAndFlush(newVerificationRequest);

      sendCodeBySMS(msisdn, code);

    } else {
      var lastVerificationRequest = verificationEntity.getLastRequestDate();
      var nextAllowedRequestAt = lastVerificationRequest.plus(waitPeriodInSec, ChronoUnit.SECONDS);
      var now = LocalDateTime.now();

      if (now.isAfter(nextAllowedRequestAt)) { //wait period between request has passed
        var newCode = codeGenerator.generateCode();
        verificationEntity.setCode(newCode);
        verificationEntity.setLastRequestDate(now);
        verificationRepo.saveAndFlush(verificationEntity);
        sendCodeBySMS(msisdn, newCode);

      } else {
        log.warn("Too many phone verification requests for {} within {} seconds period", msisdn,
            waitPeriodInSec);
        var msg = messageSource.getMessage("error.verification.too-many",
            new Object[]{waitPeriodInSec}, LocaleContextHolder.getLocale());
        throw new TooEarlyForRequestException(msg, waitPeriodInSec);
      }
    }
  }

  public boolean validateVerificationCode(PhoneVerificationCheck verificationCheck) {
    var msisdn = verificationCheck.getMsisdn();
    var savedCode = verificationRepo.getByMsisdn(msisdn);
    if (savedCode != null) {
      var codeValidUntil = savedCode.getLastRequestDate()
          .plus(validityPeriodInMin, ChronoUnit.MINUTES);
      var now = LocalDateTime.now();

      if (now.isAfter(codeValidUntil)) { // code is expired
        log.warn("Phone verification request for {} is expired", msisdn);
        var msg = messageSource.getMessage("error.verification.expired-code",
            new Object[]{validityPeriodInMin}, LocaleContextHolder.getLocale());
        throw new VerificationCodeExpiredException(msg);
      } else {
        if (verificationCheck.getCode().trim().equals(savedCode.getCode())) {
          log.info("Successfully validated verification code for {}", msisdn);
          return true;
        } else {
          log.warn("Wrong verification code {} for {}", verificationCheck.getCode(), msisdn);
          var msg = messageSource.getMessage("error.verification.wrong-code", null,
              LocaleContextHolder.getLocale());
          throw new WrongVerificationCodeException(msg);
        }
      }
    } else {
      log.warn(
          "Could not find verification code for {} id DB. It should have been generated before.",
          msisdn);
      throw new VerificationRequestNotFoundException(
          "Verification request for phone " + msisdn + " not found");
    }
  }

  private void sendCodeBySMS(String msisdn, String newCode) {
    log.info("Send verification code to {}", msisdn);
    var args = new Object[]{newCode};
    var msg = messageSource.getMessage("phone.verification.sms", args,
        LocaleContextHolder.getLocale());
    smsSender.sendSms(msisdn, msg, true);
  }
}
