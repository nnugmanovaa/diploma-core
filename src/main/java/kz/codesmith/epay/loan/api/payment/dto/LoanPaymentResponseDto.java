package kz.codesmith.epay.loan.api.payment.dto;

import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.payment.LoanPaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LoanPaymentResponseDto {
  private LoanPaymentStatus status;
  private Integer paymentId;
  private LocalDateTime paymentTime;
  private String message;
}
