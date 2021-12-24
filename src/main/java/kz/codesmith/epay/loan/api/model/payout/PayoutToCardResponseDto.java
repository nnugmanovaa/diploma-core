package kz.codesmith.epay.loan.api.model.payout;

import java.math.BigDecimal;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PayoutToCardResponseDto {
  @NotBlank
  private String url;

  @NotBlank
  private String httpMethod;

  @NotNull
  @Positive
  private BigDecimal amount;

  @NotNull
  private Integer orderId;

  private String uuid;

  private AcquiringOrderState orderState;
}
