package kz.codesmith.epay.loan.api.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "integration.telegram")
@Data
@Valid
public class TelegramServiceProperties {

  @NotBlank
  private String url;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotNull
  private Long serviceTimeout;

}
