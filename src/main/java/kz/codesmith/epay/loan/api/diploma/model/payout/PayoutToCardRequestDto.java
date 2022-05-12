package kz.codesmith.epay.loan.api.diploma.model.payout;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutToCardRequestDto {
  @NotNull
  private Integer orderId;

  private String backSuccessLink;

  private String backFailureLink;

  private String toCardsId;
}
