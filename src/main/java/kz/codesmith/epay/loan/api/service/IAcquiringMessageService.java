package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;

public interface IAcquiringMessageService {

  void firePayOneC(LoanPaymentRequestDto dto, String routingKey);
}
