package kz.codesmith.epay.loan.api.configuration.acquiring;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.acquiring")
@Data
@Validated
public class AcquiringProperties {

  @NotBlank
  private String url;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotNull
  private Long connectTimeout;

  @NotNull
  private Long readTimeout;

  @NotBlank
  private String paymentUrl;

  @NotBlank
  private String successReturnUrl;

  @NotBlank
  private String errorReturnUrl;

}
