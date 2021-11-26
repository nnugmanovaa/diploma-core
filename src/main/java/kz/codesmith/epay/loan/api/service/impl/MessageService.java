package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.model.cashout.PaymentAppEntityEventDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.ws.LoanWsPaymentDto;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logged
@RequiredArgsConstructor
public class MessageService implements IMessageService {

  private final RabbitTemplate rabbitTemplate;

  @Override
  @Transactional
  public void firePayEvent(LoanPaymentRequestDto dto, String routingKey) {
    rabbitTemplate.convertAndSend(AmqpConfig.PAYMENT_EXCHANGE_NAME, routingKey, dto);
  }

  @Override
  public void fireLoanIinStatusGetEvent(String iin, String routingKey) {
    rabbitTemplate.convertAndSend(AmqpConfig.LOAN_WAIT_EXCHANGE_NAME, routingKey, iin);
  }

  @Override
  public void fireLoanStatusGetEvent(PaymentAppEntityEventDto eventDto, String routingKey) {
    rabbitTemplate.convertAndSend(AmqpConfig.LOAN_WAIT_EXCHANGE_NAME, routingKey, eventDto);
  }

  @Override
  public void fireLoanPaymentEvent(LoanWsPaymentDto loanPaymentDto, String routingKey) {
    rabbitTemplate
        .convertAndSend(AmqpConfig.LOAN_PAYMENT_EXCHANGE_NAME, routingKey, loanPaymentDto);
  }
}
