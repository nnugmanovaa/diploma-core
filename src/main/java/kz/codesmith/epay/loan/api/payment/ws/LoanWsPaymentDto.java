package kz.codesmith.epay.loan.api.payment.ws;

import kz.integracia.PaymentApp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanWsPaymentDto {
  private PaymentApp paymentApp;
  private Integer paymentId;
  private String contractDate;
}
