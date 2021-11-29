package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.cashout.PaymentAppEntityEventDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.ws.LoanWsPaymentDto;

public interface IMessageService {

  void firePayEvent(LoanPaymentRequestDto dto, String routingKey);

  void fireLoanIinStatusGetEvent(String iin, String routingKey);

  void fireLoanStatusGetEvent(PaymentAppEntityEventDto eventDto, String routingKey);

  void fireLoanPaymentEvent(LoanWsPaymentDto loanPaymentDto, String routingKey);

  void fireCashoutEvent(PaymentAppEntityEventDto loanPaymentDto, String routingKey);
}
