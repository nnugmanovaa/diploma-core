package kz.codesmith.epay.loan.api.model.calc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonthlyPayment implements Serializable {

  public Integer order;
  public Short month;
  public String monthRu;
  public String monthKk;
  public BigDecimal amount;
  public Integer year;
}
