package kz.codesmith.epay.loan.api.service;

import java.time.LocalDate;
import java.util.List;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;
import kz.integracia.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPaymentService {

  void updatePaymentState(Integer paymentId, AcquiringOrderState orderState);

  void updateProcessingInfo(Integer paymentId, LoanPaymentResponseDto dto);

  PaymentEntity startNewPayment(OrderInitDto orderDto);

  PaymentEntity startNewPayment(Payment paymentApp);

  PaymentEntity getPayment(Integer paymentId);

  Page<LoanPaymentDto> getLoanPaymentsByOwner(LocalDate startDate,
      LocalDate endDate,
      List<MfoProcessingStatus> statuses,
      Pageable pageRequest);
}
