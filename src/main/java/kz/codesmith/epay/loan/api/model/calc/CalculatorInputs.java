package kz.codesmith.epay.loan.api.model.calc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalculatorInputs implements Serializable {

  private BigDecimal minAmount;
  private BigDecimal maxAmount;
  private BigDecimal defaultAmount;
  private Integer minPeriod;
  private Integer maxPeriod;
  private Integer defaultPeriod;
  private String defaultMethod;
  private Boolean annuityOn;
  private Boolean gradedOn;
  private List<LoanPaymentMethod> methods;
}
