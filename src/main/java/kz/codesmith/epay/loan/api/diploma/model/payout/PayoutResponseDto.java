package kz.codesmith.epay.loan.api.diploma.model.payout;

import java.time.LocalDate;
import java.util.Map;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutResponseDto {
  private String merchantName;
  private String extOrdersId;
  private Double amount;
  private String uuid;
  private LocalDate orderDate;
  private String ordersId;
  private String currency;
  private AcquiringOrderState state;
  private String paymentUrl;
  private String successReturnUrl;
  private String errorReturnUrl;
  private String cardsId;
  private Map<String, Object> payload;
  private String description;
  private PaymentType paymentType;
}
