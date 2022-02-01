package kz.codesmith.epay.loan.api.model.acquiring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackEventDto {

  private String eventId;
  private String callbackType;
  private String ordersId;
  private String ordersState;
  private Integer banksId;
  private String banksName;
  private String bankReferenceId;
  private Integer merchantsId;
  private String extOrdersId;
  private String extClientRef;
  private Object payload;
  private Integer clientsId;
  private String cardsId;
  private String subscriptionsId;
  private CardMaskedDto card;
  private Double billAmount;
  private Double totalAmount;
  private Double authAmount;
  private EventCommissionInfo operatorsCommission;
  private EventCommissionInfo bankCommission;
  private String currency;
  private String message;
  private String description;
  private boolean is2phase;
  private boolean is3ds;
  private boolean isSubscription;
}