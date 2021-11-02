package kz.codesmith.epay.loan.api.component.coid;

import java.io.File;
import kz.codesmith.epay.loan.api.exception.UserIdentityFailedException;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Logged
@Component
@RequiredArgsConstructor
public class CoidConnector {

  private final CoidConnectorProperties props;
  private final RestTemplate restTemplate;
  private final MessageSource messageSource;

  public IdentityMatchResponse matchUserIdentityByPhoto(String iin, File userPhoto,
      String idempotencyKey) {
    log.info("Match user identity iin={}, idempotencyKey={}", iin, idempotencyKey);

    var url = String.format("%s/identity/%s/%s", props.getUrl(), iin, idempotencyKey);
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> postFormFields = new LinkedMultiValueMap<>();
    postFormFields.add("photo", new FileSystemResource(userPhoto));
    var request = new HttpEntity<>(postFormFields, headers);
    var response = restTemplate.postForEntity(url, request, IdentityMatchResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      log.error("User identity match failed for iin={} idempotencyKey{}", iin, idempotencyKey);
      var msg = messageSource.getMessage("error.try-later", null, LocaleContextHolder.getLocale());
      throw new UserIdentityFailedException(msg);
    }
  }

  public IdentityMatchResponse getMatchingResult(String iin, String idempotencyKey) {
    log.info("Get user identity matching result iin={}, idempotencyKey={}", iin, idempotencyKey);

    var url = String.format("%s/identity/%s/%s", props.getUrl(), iin, idempotencyKey);
    var response = restTemplate.getForEntity(url, IdentityMatchResponse.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      return response.getBody();
    } else {
      log.error("User identity match result fetch failed for iin={} idempotencyKey{}", iin,
          idempotencyKey);
      var msg = messageSource.getMessage("error.try-later", null, LocaleContextHolder.getLocale());
      throw new UserIdentityFailedException(msg);
    }
  }

}
