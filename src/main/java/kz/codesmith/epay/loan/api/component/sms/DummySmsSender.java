package kz.codesmith.epay.loan.api.component.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DummySmsSender implements SmsSender {

  private String msgTail;

  private String msgTailDebug;

  @Override
  public void sendSms(String msisdn, String text, boolean attachTail) {
    var message = text;
    if (attachTail) {
      message += getTail();
    }
    log.info("Successfully sent SMS to {} with attachTail={} msg={}", msisdn, attachTail,
        message);
  }

  private String getTail() {
    var tail = "";
    if (msgTail != null) {
      tail = "\n" + msgTail;
    }
    return tail;
  }
}
