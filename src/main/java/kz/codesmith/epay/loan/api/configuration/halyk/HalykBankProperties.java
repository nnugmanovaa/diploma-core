package kz.codesmith.epay.loan.api.configuration.halyk;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "integration.halyk")
@Data
@Validated
public class HalykBankProperties {

  @NotBlank
  private String certificateId;

  @NotBlank
  private String certificate;

  @NotBlank
  private String signCertificateAlias;

  @NotBlank
  private String verifyCertificateAlias;

  @NotBlank
  private String password;

  @NotBlank
  private String merchantId;

  @NotBlank
  private String merchantMainTerminalId;

  @NotBlank
  private String merchantName;

  @NotBlank
  private String cardIdFrom;

  @NotBlank
  private String cardTransferSubmitUrl;

  @NotBlank
  private String cardTransferTemplate;

  @NotBlank
  private String postLink;

}
