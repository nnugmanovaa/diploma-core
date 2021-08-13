package kz.codesmith.epay.loan.api.payment;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.mfo-app")
@Data
@Validated
public class MfoAppProperties {

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String serviceName;

  @NotBlank
  private String url;

  @NotNull
  private Long readTimeout;
}
