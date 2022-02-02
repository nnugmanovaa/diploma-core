package kz.codesmith.epay.loan.api.model.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRepaymentSchedule {
  private OrderDetailsSchedule orderDetailsSchedule;
  private boolean hasActiveLoan;
  private String message;
}
