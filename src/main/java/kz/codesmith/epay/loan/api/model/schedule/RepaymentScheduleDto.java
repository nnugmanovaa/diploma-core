package kz.codesmith.epay.loan.api.model.schedule;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepaymentScheduleDto {
  private Integer repaymentScheduleId;
  private Integer orderId;
  private BigDecimal amountOverpayment;
  private BigDecimal amountRemain;
}
