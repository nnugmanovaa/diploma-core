package kz.codesmith.epay.loan.api.repository.acquiring;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramPaymentDto {
  private Integer paymentId;
  private String mfoProcessingMessage;
  private AcquiringOrderState orderState;
}
