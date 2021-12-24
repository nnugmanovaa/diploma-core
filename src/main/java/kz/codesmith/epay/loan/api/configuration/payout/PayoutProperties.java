package kz.codesmith.epay.loan.api.configuration.payout;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.payout")
@Data
@Validated
public class PayoutProperties {
  private String cardIdFrom;
  private Boolean enabled;
  private String username;
  private String password;
  private String callbackSuccessUrl;
  private String callbackErrorUrl;
}
