package kz.codesmith.epay.loan.api.configuration.mfs.core;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.mfs.core")
@Data
@Validated
public class MfsCoreProperties {

  @NotBlank
  private String url;

  @NotBlank
  private String username;

  @NotBlank
  private String password;

  @NotBlank
  private String agentTopUsername;

  @NotBlank
  private String agentTopPassword;

  @NotNull
  private Long serviceTimeout;

  @NotBlank
  private String signInUrl;

  @NotBlank
  private String getUserUrl;

  @NotBlank
  private String getClientUrl;

  @NotBlank
  private String getPaymentUrl;

  @NotBlank
  private String clientsProfileUrl;
  @NotBlank
  private String agentName;

  @NotBlank
  private String agentPassword;

  @NotBlank
  private String initTopUpUrl;

  @NotBlank
  private String returnPaymentUrl;

  @NotBlank
  private String regularServiceId;

  @NotBlank
  private String earlyPartialServiceId;

  @NotBlank
  private String earlyFullServiceId;
}
