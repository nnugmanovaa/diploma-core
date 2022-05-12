package kz.codesmith.epay.loan.api.model.acquiring;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventCommissionInfo {

  private BigDecimal effectiveValue;
  private String effectiveValueType;
  private BigDecimal amount;

}