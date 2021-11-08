package kz.codesmith.epay.loan.api.component.codegen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "app.phone-verification.dummy-code",
    havingValue = "true",
    matchIfMissing = false)
public class DummyCodeGenerator implements CodeGenerator {

  @Value("${app.phone-verification.dummy-code-value}")
  private String dummyValue;

  @Override
  public String generateCode() {
    return dummyValue;
  }
}
