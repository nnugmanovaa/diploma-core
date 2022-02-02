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
public class ScheduleItemsDto {
  private Integer itemId;
  private Integer repaymentScheduleId;
  private Integer paymentId;
  private Integer number;
  private String paymentDate;
  private BigDecimal amountToBePaid;
  private BigDecimal reward;
  private BigDecimal totalAmountDebt;
  private String status;
}
