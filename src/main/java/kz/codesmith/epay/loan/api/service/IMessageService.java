package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.ws.LoanWsPaymentDto;

public interface IMessageService {

  void firePayEvent(LoanPaymentRequestDto dto, String routingKey);

  void fireLoanStatusGetEvent(String iin, String routingKey);

  void fireLoanPaymentEvent(LoanWsPaymentDto loanPaymentDto, String routingKey);
}
