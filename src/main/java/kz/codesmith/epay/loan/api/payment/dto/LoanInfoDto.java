package kz.codesmith.epay.loan.api.payment.dto;

import java.math.BigDecimal;
import kz.codesmith.epay.loan.api.payment.LoanStatus;
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
  private BigDecimal arrears;
  private String plannedPaymentDate;
  private BigDecimal plannedPaymentAmount;
  private BigDecimal minimumAmountOfPartialRepayment;
  private LoanStatusDto loanStatus;
}
