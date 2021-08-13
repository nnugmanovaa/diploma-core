package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.model.core.SignInRequest;
import kz.codesmith.epay.loan.api.model.core.SignInResponse;
import kz.codesmith.epay.loan.api.model.exception.SignInFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreAuthService {

  private final MfsCoreProperties coreProperties;
  private final RestTemplate restTemplate;
  private final MessageSource messageSource;

  public SignInResponse signIn(String username, String password) {
    log.info("SignIn attempt for {}", username);

    var signIn = new SignInRequest(username, password);
    var entity = new HttpEntity(signIn);

    var url = coreProperties.getUrl() + coreProperties.getSignInUrl();
    try {
      var response = restTemplate.postForEntity(url, entity, SignInResponse.class);
      return response.getBody();
    } catch (HttpClientErrorException e) {
      log.error("SignIn attempt failed for {}. {} returned '{}'", username, url, e.getStatusCode());
      var errMsg = messageSource
          .getMessage("error.auth-failed", null, LocaleContextHolder.getLocale());
      throw new SignInFailedException(errMsg);
    }
  }
}
