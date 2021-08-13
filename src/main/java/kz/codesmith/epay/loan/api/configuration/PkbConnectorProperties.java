package kz.codesmith.epay.loan.api.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties("integration.pkb-connector")
public class PkbConnectorProperties {
  private String url;
  private String username;
  private String password;
}
