package kz.codesmith.epay.loan.api.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderScoringVariables {
  private Integer orderId;
  private OrderState status;
  private BigDecimal loanInterestRate;
  private BigDecimal loanEffectiveRate;
  private String orderExtRefId;
  private LocalDateTime orderExtRefTime;
}
