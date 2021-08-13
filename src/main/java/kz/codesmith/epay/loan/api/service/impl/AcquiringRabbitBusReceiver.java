package kz.codesmith.epay.loan.api.service.impl;

import java.util.Objects;
import kz.codesmith.epay.core.shared.model.analyzes.LoanPaymentErrorDto;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.payment.ILoanPayment;
import kz.codesmith.epay.loan.api.payment.LoanPaymentStatus;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.codesmith.epay.telegram.gw.controller.ITelegramBotController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AcquiringRabbitBusReceiver {

  private final IPaymentService paymentService;
  private final ILoanPayment loanPaymentService;
  private final ITelegramBotController telegramBotController;

  @RabbitListener(
      queues = {AmqpConfig.PAYMENT_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void payOneC(LoanPaymentRequestDto dto) {
    log.info("Received payOneC call");

    try {
      if (Objects.nonNull(dto)) {
        LoanPaymentResponseDto responseDto = loanPaymentService.processPayment(dto);
        if (responseDto.getStatus() == LoanPaymentStatus.ERROR) {
          telegramBotController.sendLoanPaymentErrorNotification(LoanPaymentErrorDto.builder()
              .paymentId(responseDto.getPaymentId())
              .comment(responseDto.getMessage())
              .build());
        }
        paymentService.updateProcessingInfo(dto.getPaymentId(), responseDto);
      } else {
        log.error("The request data is null");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }
}
