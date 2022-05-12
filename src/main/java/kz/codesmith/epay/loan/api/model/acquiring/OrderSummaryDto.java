package kz.codesmith.epay.loan.api.model.acquiring;

import java.time.LocalDate;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSummaryDto {
  private String merchantName;
  private String extOrdersId;
  private Double amount;
  private String uuid;
  private LocalDate orderDate;
  private String ordersId;
  private String currency;
  private AcquiringOrderState state;
  private CardMaskedDto card;
  private String bankReferenceId;
  private String bankReferenceTime;
  private String approvalCode;
  private String message;
  private String paymentResponseCode;
  private String frame3dsUrl;
  private String paymentUrl;
  private String successReturnUrl;
  private String failureReturnUrl;
  private Integer clientsId;
  private String extClientRef;
  private String cardsId;
  private Map<String, Object> payload;
  private IpDetailsDto ipDetails;
  private String description;
}