package kz.codesmith.epay.loan.api.model.schedule;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsSchedule {
  private String contract;
  private BigDecimal monthPayment;
  private BigDecimal totalAmount;
  private LocalDate contractDate;
  private Integer period;
  private BigDecimal amountRemain;
  private BigDecimal amountOverPayment;
  private List<ScheduleItemsDto> scheduleItems;
}
