package kz.codesmith.epay.loan.api.payment.services;

import java.math.BigInteger;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Configuration
@ConfigurationProperties(prefix = "payment-services")
public class LoanPaymentServices {
  @NotNull
  private Long plannedRePaymentProductId;

  @NotNull
  private Long partialRePaymentProductId;

  @NotNull
  private Long totalRePaymentProductId;
}
