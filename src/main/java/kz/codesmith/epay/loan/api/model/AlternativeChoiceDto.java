package kz.codesmith.epay.loan.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AlternativeChoiceDto {

  private BigDecimal loanAmount;
  private Integer loanMonthPeriod;
  private Integer orderId;
  private LocalDateTime orderTime;
}
