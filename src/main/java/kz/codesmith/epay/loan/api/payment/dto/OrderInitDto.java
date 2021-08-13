package kz.codesmith.epay.loan.api.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.payment.LoanRepayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderInitDto {
  private String iin;
  private String contractNumber;
  private String contractDate;
  private LoanRepayType loanRepayType;
  private String productExtRefId;
  private BigDecimal amount;
  private LocalDateTime orderTime;
}
