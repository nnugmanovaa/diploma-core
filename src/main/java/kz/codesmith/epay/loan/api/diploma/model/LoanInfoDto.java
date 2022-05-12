package kz.codesmith.epay.loan.api.diploma.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanInfoDto {
  private String contractNumber;
  private String contractDate;
  private BigDecimal amountOfDebt;
  private BigDecimal plannedPaymentAmount;
  private Integer loanPeriodMonths;
}
