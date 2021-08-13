package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationRequest;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringStatusResponse;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;

public interface IAcquiringService {

  AcquiringPaymentResponse initPayment(OrderInitDto orderDto);

  AcquiringStatusResponse getPaymentStatus(Integer orderId);

  AcquiringNotificationResponse processNotification(
      AcquiringNotificationRequest notificationRequest);
}
