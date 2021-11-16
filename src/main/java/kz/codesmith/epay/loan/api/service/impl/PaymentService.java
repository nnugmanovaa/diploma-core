package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.payment.LoanPaymentConstants;
import kz.codesmith.epay.loan.api.payment.LoanPaymentStatus;
import kz.codesmith.epay.loan.api.payment.LoanRepayType;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.integracia.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

  private final PaymentRepository paymentRepository;

  @Override
  @Transactional
  public void updatePaymentState(Integer paymentId, AcquiringOrderState orderState) {
    PaymentEntity payment = getPayment(paymentId);
    payment.setOrderState(orderState);
    paymentRepository.save(payment);
  }

  @Override
  @Transactional
  public void updateProcessingInfo(Integer paymentId, LoanPaymentResponseDto dto) {
    MfoProcessingStatus processingStatus =
        (dto.getStatus() == LoanPaymentStatus.SUCCESS) ? MfoProcessingStatus.PROCESSED
            : MfoProcessingStatus.ERROR_IN_PROCESSING;

    PaymentEntity payment = getPayment(paymentId);
    payment.setMfoProcessingStatus(processingStatus);
    payment.setMfoProcessingMessage(dto.getMessage());
    paymentRepository.save(payment);
  }

  @Override
  @Transactional
  public PaymentEntity startNewPayment(OrderInitDto orderDto) {
    PaymentEntity payment = new PaymentEntity();
    payment.setAmount(orderDto.getAmount());
    payment.setClientRef(orderDto.getIin());
    payment.setContractDate(orderDto.getContractDate());
    payment.setContractNumber(orderDto.getContractNumber());
    payment.setLoanRepayType(orderDto.getLoanRepayType());
    payment.setDescription(LoanPaymentConstants.LOAN_PAYMENT_INIT_DESCRIPTION);
    payment.setCurrency(LoanPaymentConstants.KZT_PAYMENT_CURRENCY);
    return paymentRepository.save(payment);
  }

  @Override
  public PaymentEntity startNewPayment(Payment paymentApp) {
    PaymentEntity payment = new PaymentEntity();
    payment.setAmount(paymentApp.getData().getSum());
    payment.setClientRef(paymentApp.getData().getAccount());
    payment.setContractDate(String
        .valueOf(paymentApp
            .getData()
            .getContractDate()));
    payment.setContractNumber(paymentApp.getData().getContractNumber());
    payment.setLoanRepayType(setProperRepayType(paymentApp
        .getData()
        .getPayType()
        .intValue()));
    payment.setDescription(LoanPaymentConstants.LOAN_PAYMENT_INIT_DESCRIPTION);
    payment.setCurrency(LoanPaymentConstants.KZT_PAYMENT_CURRENCY);
    return paymentRepository.save(payment);
  }

  @Override
  public PaymentEntity getPayment(Integer paymentId) {
    return paymentRepository.findById(paymentId)
        .orElseThrow(() -> new NotFoundApiServerException(
            ApiErrorTypeParamValues.PAYMENT,
            paymentId
        ));
  }

  private LoanRepayType setProperRepayType(Integer type) {
    if (type == 1) {
      return LoanRepayType.PLANNED_REPAYMENT;
    } else if (type == 2) {
      return LoanRepayType.PARTIAL_REPAYMENT;
    } else if (type == 3) {
      return LoanRepayType.TOTAL_REPAYMENT;
    } else {
      throw new IllegalStateException();
    }
  }
}
