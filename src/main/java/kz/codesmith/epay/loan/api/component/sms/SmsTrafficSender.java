package kz.codesmith.epay.loan.api.component.sms;

import kz.codesmith.epay.loan.api.exception.FailedToSendSmsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    value = "app.sms-sender.dummy",
    havingValue = "false",
    matchIfMissing = true)
public class SmsTrafficSender implements SmsSender {

  public static final String UTF8_FLAG = "5";
  private final RestTemplate restTemplate;

  @Value("${app.sms-sender.url}")
  private String smsTrafficUrl;

  @Value("${app.sms-sender.username}")
  private String username;

  @Value("${app.sms-sender.password}")
  private String password;

  @Value("${app.sms-sender.sender}")
  private String sender;

  @Value("${app.sms-sender.msg-tail}")
  private String msgTail;


  @Override
  public void sendSms(String msisdn, String text, boolean attachTail) {
    MultiValueMap<String, String> postFormFields = new LinkedMultiValueMap<>();
    postFormFields.add("login", username);
    postFormFields.add("password", password);
    if (sender != null && !sender.isBlank()) {
      postFormFields.add("originator", sender);
    }
    postFormFields.add("phones", msisdn);
    var message = text;
    if (attachTail) {
      message += getTail();
    }
    postFormFields.add("message", message);
    postFormFields.add("rus", UTF8_FLAG);

    HttpEntity<MultiValueMap<String, String>> request =
        new HttpEntity<>(postFormFields, getHeaders());
    ResponseEntity<String> response = restTemplate.postForEntity(smsTrafficUrl, request,
        String.class);

    if (response.getStatusCode().is2xxSuccessful()) {
      var respBody = response.getBody();
      if (respBody.contains("<code>0</code>")) {
        log.info("Successfully sent SMS to {} with attachTail={}", msisdn, attachTail);
      } else {
        if (respBody.contains("<code>411</code>")) {
          log.error("Failed to send SMS to {} due to authentication failure", msisdn);
        } else if (respBody.contains("<code>427</code>")) {
          log.warn("Failed to send SMS to {} due to wrong phone number format", msisdn);
        } else {
          log.warn("Failed to send SMS to {}. {}", msisdn, respBody);
        }
        throw new FailedToSendSmsException("Failed to send SMS");
      }

    }
  }

  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return headers;
  }

  private String getTail() {
    var tail = "";

    if (msgTail != null) {
      tail = "\n" + msgTail;
    }

    return tail;
  }
}
