package kz.codesmith.epay.loan.api.model.payout;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutUpdateStateEventDto {
  private Integer paymentId;
  private String extRefId;
  private String extUuid;
  private LocalDate extRefTime;
  private Integer loanOrderId;
}
