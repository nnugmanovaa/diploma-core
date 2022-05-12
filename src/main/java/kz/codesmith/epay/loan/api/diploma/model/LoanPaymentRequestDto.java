package kz.codesmith.epay.loan.api.diploma.model;

import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

  @NotNull
  private BigDecimal amount;

  private Integer orderId;
}
