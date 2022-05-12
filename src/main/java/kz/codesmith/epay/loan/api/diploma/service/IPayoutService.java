package kz.codesmith.epay.loan.api.diploma.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;

public interface IPayoutService {
  List<OrderState> initPayoutStates = Arrays.asList(OrderState.CONFIRMED,
      OrderState.CASH_OUT_CARD_INITIALIZED, OrderState.CASH_OUT_CARD_FAILED);

  PayoutToCardResponseDto initPayoutToCard(PayoutToCardRequestDto request);

  OrderSummaryDto getOrderStatus(PayoutUpdateStateEventDto updateStateEventDto);
}
