package kz.codesmith.epay.loan.api.payment.dto;

import kz.codesmith.epay.loan.api.payment.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusDto {
  private LoanStatus loanStatus;
  private String description;
}
