package kz.codesmith.epay.loan.api.component.coid;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties("app.coid-connector")
public class CoidConnectorProperties {

  private String url;
  private String username;
  private String password;
}
