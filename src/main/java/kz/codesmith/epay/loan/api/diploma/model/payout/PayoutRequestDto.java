package kz.codesmith.epay.loan.api.diploma.model.payout;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutRequestDto {
  private String cardsId;
  private String toCardsId;
  private Double amount;
  private String currency;
  private String extOrdersId;
  private String description;
  private String successReturnUrl;
  private String errorReturnUrl;
  private Map<String, Object> payload;
  private String extClientRef;
  private String callbackSuccessUrl;
  private String callbackErrorUrl;
}
