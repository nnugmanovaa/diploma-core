package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;

public interface IPaymentService {

  void updatePaymentState(Integer paymentId, AcquiringOrderState orderState);

  void updateProcessingInfo(Integer paymentId, LoanPaymentResponseDto dto);

  PaymentEntity startNewPayment(OrderInitDto orderDto);

  PaymentEntity getPayment(Integer paymentId);
}
