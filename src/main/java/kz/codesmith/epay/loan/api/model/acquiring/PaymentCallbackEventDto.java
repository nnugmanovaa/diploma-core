package kz.codesmith.epay.loan.api.model.acquiring;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackEventDto {

  private ZonedDateTime eventTime;
  private String eventId;
  private String callbackType;
  private String ordersId;
  private ZonedDateTime ordersTime;
  private String ordersState;
  private ZonedDateTime ordersStateTime;
  private Integer banksId;
  private String banksName;
  private String bankReferenceId;
  private ZonedDateTime bankReferenceTime;
  private Integer merchantsId;
  private String extOrdersId;
  private ZonedDateTime extOrdersTime;
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