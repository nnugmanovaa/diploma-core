package kz.codesmith.epay.loan.api.diploma.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
  private Integer orderId;
}
