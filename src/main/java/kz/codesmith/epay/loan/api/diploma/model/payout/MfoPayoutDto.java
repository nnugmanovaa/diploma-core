package kz.codesmith.epay.loan.api.diploma.model.payout;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfoPayoutDto {
  private Integer loanOrderId;
  private AcquiringOrderState orderState;
  private String body;
}
