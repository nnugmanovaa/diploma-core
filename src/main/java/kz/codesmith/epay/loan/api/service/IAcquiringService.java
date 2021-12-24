package kz.codesmith.epay.loan.api.service;

import java.time.LocalDate;
import java.util.List;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationRequest;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringStatusResponse;
import kz.codesmith.epay.loan.api.model.acquiring.CardSaveDto;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.SaveCardRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;

public interface IAcquiringService {

  AcquiringPaymentResponse initPayment(OrderInitDto orderDto);

  AcquiringStatusResponse getPaymentStatus(Integer orderId);

  OrderSummaryDto getOrderStatus(String extOrderId, String uuid, LocalDate extRefTime);

  AcquiringNotificationResponse processNotification(
      AcquiringNotificationRequest notificationRequest);

  OrderSummaryDto saveCard(SaveCardRequestDto requestDto);

  List<CardSaveDto> getAllSavedCards(String extClientRef);

  void deleteSavedCard(String cardsId);
}
