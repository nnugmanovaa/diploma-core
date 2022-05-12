package kz.codesmith.epay.loan.api.diploma.service.impl;

import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.diploma.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements IMessageService {
  private final RabbitTemplate rabbitTemplate;

  @Override
  public void fireLoanStatusGetEvent(PayoutUpdateStateEventDto eventDto, String routingKey) {
    rabbitTemplate.convertAndSend(AmqpConfig.LOAN_WAIT_EXCHANGE_NAME, routingKey, eventDto);
  }
}
