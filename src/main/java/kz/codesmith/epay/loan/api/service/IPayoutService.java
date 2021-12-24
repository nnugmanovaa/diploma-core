package kz.codesmith.epay.loan.api.service;

import java.util.Arrays;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardResponseDto;

public interface IPayoutService {
  List<OrderState> initPayoutStates = Arrays.asList(OrderState.CONFIRMED,
      OrderState.CASH_OUT_CARD_INITIALIZED, OrderState.CASH_OUT_CARD_FAILED);

  PayoutToCardResponseDto initPayoutToCard(PayoutToCardRequestDto request);

  void acceptPayoutCallbackRequest(String body);

  void checkPayoutStatuses();
}
