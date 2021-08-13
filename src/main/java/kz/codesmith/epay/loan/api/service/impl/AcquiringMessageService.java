package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.service.IAcquiringMessageService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Logged
@RequiredArgsConstructor
public class AcquiringMessageService implements IAcquiringMessageService {

  private final RabbitTemplate rabbitTemplate;

  @Override
  @Transactional
  public void firePayOneC(LoanPaymentRequestDto dto, String routingKey) {
    rabbitTemplate.convertAndSend(AmqpConfig.PAYMENT_EXCHANGE_NAME, routingKey, dto);
  }
}
