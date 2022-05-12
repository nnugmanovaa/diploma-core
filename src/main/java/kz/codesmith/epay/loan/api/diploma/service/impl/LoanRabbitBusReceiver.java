package kz.codesmith.epay.loan.api.diploma.service.impl;

import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.diploma.service.IOrdersService;
import kz.codesmith.epay.loan.api.diploma.service.IPayoutService;
import kz.codesmith.epay.loan.api.diploma.service.IScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanRabbitBusReceiver {

  private final RabbitTemplate rabbitTemplate;
  private final IPayoutService payoutService;
  private final IScoringService scoringService;

  public static final String ORIGIN_EXCHANGE_HEADER = "x-first-death-exchange";

  @RabbitListener(
      group = "epay-core",
      queues = {AmqpConfig.LOAN_STATUS_DLX_QUEUE_NAME},
      containerFactory = "jsaFactory"
  )
  public void dlxLoanStatusProcesssing(Message message) {
    try {
      log.info("Init DLX loan processing");
      String originalRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
      rabbitTemplate.convertAndSend(AmqpConfig.LOAN_STATUSES_EXCHANGE_NAME, originalRoutingKey,
          message);
    } catch (Exception e) {
      log.error("Message cannot be reprocessed: ", e);
    }
  }

  @RabbitListener(
      queues = {AmqpConfig.LOAN_STATUSES_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public int getLoanStatusesListener(PayoutUpdateStateEventDto entityEventDto) {
    log.info("STATUS CHECK CALLED");
    if (entityEventDto.getAction().equals("payout")) {
      payoutService.getOrderStatus(entityEventDto);
      return 1;
    }
    scoringService.getOrderStatus(entityEventDto);
    return 0;
  }

  @RabbitListener(
      group = "epay-core",
      queues = {AmqpConfig.DLX_QUEUE_NAME},
      containerFactory = "jsaFactory"
  )
  public void dlxProcesssing(Message message) {
    try {
      log.info("Init dlx processing");
      String exchangeName = (String) message.getMessageProperties()
          .getHeaders()
          .get(ORIGIN_EXCHANGE_HEADER);
      String originalRoutingKey = message.getMessageProperties().getReceivedRoutingKey();
      log.info("DLX requeuing for exchange: {}, with routing key: {}", exchangeName,
          originalRoutingKey);
      rabbitTemplate.convertAndSend(exchangeName, originalRoutingKey, message);
    } catch (Exception e) {
      log.error("Message cannot be reprocessed: ", e);
    }
  }
}
