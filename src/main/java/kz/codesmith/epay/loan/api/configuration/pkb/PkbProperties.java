package kz.codesmith.epay.loan.api.configuration.pkb;

import java.time.Duration;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.pkb")
@Data
@Validated
public class PkbProperties {

  @NotBlank
  private String url;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotNull
  private Long serviceTimeout;

  @NotNull
  private Duration cacheDurationTtl;

}
