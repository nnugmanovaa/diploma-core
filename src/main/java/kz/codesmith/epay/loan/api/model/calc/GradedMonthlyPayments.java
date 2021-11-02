package kz.codesmith.epay.loan.api.model.calc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GradedMonthlyPayments implements Serializable {

  private List<MonthlyPayment> monthlyPayments;
}
