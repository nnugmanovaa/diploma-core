package kz.codesmith.epay.loan.api.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.payment.LoanRepayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanPaymentRequestDto {
  @NotBlank
  private String clientRef;

  @NotBlank
  private String contractNumber;

  @NotBlank
  private String contractDate;

  private LoanRepayType loanRepayType;

  private Integer paymentId;

  private LocalDateTime insertedTime;

  @NotNull
  private BigDecimal amount;
}
