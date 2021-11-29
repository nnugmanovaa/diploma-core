package kz.codesmith.epay.loan.api.model.cashout;

import java.math.BigDecimal;
import kz.integracia.Contract;
import kz.integracia.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAppEntityEventDto {
  private String iin;
  private String extRefId;
  private String msisdn;
  private Double returnAmount;
  private BigDecimal expectedAmount;
  private Integer serviceId;
  private Contract contract;

  private Payment payment;
}
