package kz.codesmith.epay.loan.api.component.codegen;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "app.phone-verification.dummy-code",
    havingValue = "false",
    matchIfMissing = true)
public class RandomCodeGenerator implements CodeGenerator {

  @Value("${app.phone-verification.code-length:6}")
  private int codeLength;

  @Override
  public String generateCode() {
    return RandomStringUtils.randomNumeric(codeLength);
  }
}
