package kz.codesmith.epay.loan.api.model;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.payintech.ListLoanMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculationDto {

  @NotBlank
  private String creditProduct;

  @NotNull
  @Positive
  private BigDecimal amount;

  @NotNull
  private Integer loanMonthPeriod;

  @NotNull
  private float loanInterestRate;

  @NotNull
  private ListLoanMethod loanType;

}
