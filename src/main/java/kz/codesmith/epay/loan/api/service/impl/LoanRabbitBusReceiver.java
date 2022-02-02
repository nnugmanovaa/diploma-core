package kz.codesmith.epay.loan.api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeFactory;
import kz.codesmith.epay.core.shared.model.analyzes.LoanPaymentErrorDto;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.core.shared.model.services.FieldType;
import kz.codesmith.epay.core.shared.model.services.ServiceField;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.domain.RepaymentScheduleEntity;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.cashout.PaymentAppEntityEventDto;
import kz.codesmith.epay.loan.api.model.core.CorePaymentReturnDto;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.payout.MfoPayoutDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.payment.ILoanPayment;
import kz.codesmith.epay.loan.api.payment.LoanPaymentConstants;
import kz.codesmith.epay.loan.api.payment.LoanPaymentStatus;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.payment.ws.LoanWsPaymentDto;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.repository.RepaymentScheduleRepository;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.codesmith.epay.telegram.gw.controller.ITelegramBotController;
import kz.integracia.Contract;
import kz.payintech.InformationTheAgreement;
import kz.payintech.ResultResponsegetUserContractsList;
import kz.payintech.siteexchange.SiteExchangePortType;
import kz.pitech.mfo.PaymentApp;
import kz.pitech.mfo.PaymentServicesPortType;
import kz.pitech.mfo.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.common.util.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoanRabbitBusReceiver {

  private final IPaymentService paymentService;
  private final ILoanPayment loanPaymentService;
  private final ITelegramBotController telegramBotController;
  private final LoanOrdersRepository loanOrdersRepository;
  private final ModelMapper mapper;
  private final SiteExchangePortType siteExchangePortType;
  private final PaymentServicesPortType servicesPortType;
  private final RabbitTemplate rabbitTemplate;
  private final CoreReturnService returnService;
  private final IMessageService messageService;
  private final CoreTopUpService topUpService;
  private final IAcquiringService acquiringService;
  private final ILoanOrdersService loanOrdersService;
  private final PaymentRepository paymentRepository;
  private final RepaymentScheduleRepository scheduleRepository;

  public static final String SUCCESS_RESPONSE_RESULT = "0";
  public static final String ORIGIN_EXCHANGE_HEADER = "x-first-death-exchange";
  public static final String LOAN_REDEEMED_STATUS = "Redeemed";
  public static final String LOAN_REDEEMED_EARLY_STATUS = "RedeemedEarly";

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
        if (responseDto.getStatus() == LoanPaymentStatus.SUCCESS) {
          substractLoanRemainAmount(dto.getPaymentId(), dto.getAmount());
        }
        paymentService.updateProcessingInfo(dto.getPaymentId(), responseDto);
      } else {
        log.error("The request data is null");
      }
    } catch (Exception e) {
      log.error(e.getMessage());
    }

  }

  private void substractLoanRemainAmount(Integer paymentId, BigDecimal amount) {
    Integer loanOrderId = paymentService
        .getPayment(paymentId)
        .getLoanOrderId();

    Optional<RepaymentScheduleEntity> scheduleEntity = scheduleRepository
        .findByOrderId(loanOrderId);

    scheduleEntity.ifPresent(entity -> {
      BigDecimal remainAmount = entity.getAmountRemain().subtract(amount);
      entity.setAmountRemain(remainAmount);
      scheduleRepository.save(entity);
    });
  }


  @RabbitListener(
      queues = {AmqpConfig.LOAN_STATUSES_IIN_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public void getLoanStatusesIinListener(String iin) {
    getLoanStatuses(iin, null);
  }

  @RabbitListener(
      queues = {AmqpConfig.LOAN_STATUSES_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public void getLoanStatusesListener(PaymentAppEntityEventDto entityEventDto) {
    getLoanStatuses(entityEventDto.getPayment().getData().getAccount(), entityEventDto);
  }

  public void getLoanStatuses(String iin, PaymentAppEntityEventDto entityEventDto) {
    log.info("Processing status update");
    List<OrderDto> orderDtos = getClientOpenLoans(iin);
    if (!CollectionUtils.isEmpty(orderDtos)) {
      List<InformationTheAgreement> contracts = getClientContracts(iin);
      if (!CollectionUtils.isEmpty(contracts)) {
        orderDtos.forEach(orderDto -> {
          updateLoanStatus(orderDto, contracts, entityEventDto);
        });
      }
    }
  }

  @RabbitListener(
      queues = {AmqpConfig.CASHOUT_PAYMENT_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public void initCashoutForRemain(PaymentAppEntityEventDto entityEventDto) {
    try {
      Contract checkContract = entityEventDto.getContract();
      if (checkContract.getAmountOfDebt().compareTo(entityEventDto.getExpectedAmount()) != -1) {
        log.info("No need to cashout");
      } else {
        log.info("Init cashout");
        Double billAmount = (entityEventDto.getExpectedAmount()
            .subtract(checkContract.getAmountOfDebt())).doubleValue();

        ServiceField account = ServiceField.builder()
            .name("account")
            .value(entityEventDto.getPayment()
                .getData()
                .getAccount())
            .type(FieldType.STRING)
            .build();

        log.info("Init returning");
        CorePaymentReturnDto returnDto = CorePaymentReturnDto.builder()
            .fields(new ArrayList<>())
            .billAmount(-billAmount)
            .servicesId(entityEventDto.getServiceId())
            .clientName(entityEventDto.getMsisdn())
            .fields(Collections.singletonList(account))
            .build();

        log.info("Calling returning process");
        returnService.initReturnPayment(returnDto);
      }
    } catch (Exception e) {
      log.error("Error while cashout", e);
    }
  }

  @Transactional
  @RabbitListener(
      queues = {AmqpConfig.PAYOUT_PAYMENT_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public void initPayoutPaymentStatusUpdate(PayoutUpdateStateEventDto eventDto) {
    try {
      OrderSummaryDto orderSummaryDto = acquiringService.getOrderStatus(
          eventDto.getExtRefId(),
          eventDto.getExtUuid(),
          eventDto.getExtRefTime());
      updateLoanPayoutOrderState(eventDto.getLoanOrderId(), orderSummaryDto.getState(), null);
      PaymentEntity entity = paymentRepository.findById(eventDto.getPaymentId())
          .orElseThrow(() -> new NotFoundApiServerException(
              ApiErrorTypeParamValues.PAYMENT,
              eventDto.getPaymentId()
          ));
      entity.setOrderState(orderSummaryDto.getState());
      paymentRepository.save(entity);
    } catch (Exception e) {
      log.error("Error while payout status update", e);
    }
  }

  @RabbitListener(
      queues = {AmqpConfig.MFO_PAYOUT_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  public void initPayoutPaymentStatusUpdate(MfoPayoutDto payoutDto) {
    updateLoanPayoutOrderState(
        payoutDto.getLoanOrderId(),
        payoutDto.getOrderState(),
        payoutDto.getBody()
    );
  }

  private void updateLoanStatus(OrderDto orderDto, List<InformationTheAgreement> contracts,
      PaymentAppEntityEventDto entityEventDto) {
    contracts.forEach(contract -> {
      if (contract.getNumber().equals(orderDto.getContractExtRefId())) {
        setLoanPaymentStatus(orderDto, contract, entityEventDto);
      }
    });
  }

  private void setLoanPaymentStatus(OrderDto orderDto, InformationTheAgreement contract,
      PaymentAppEntityEventDto entityEventDto) {
    switch (contract.getStatus()) {
      case LOAN_REDEEMED_EARLY_STATUS :
      case LOAN_REDEEMED_STATUS : {
        orderDto.setStatus(OrderState.CLOSED);
        loanOrdersRepository.save(mapper.map(orderDto, OrderEntity.class));
        log.info("Order status updated for orderId={}, set={}",
            orderDto.getOrderId(), OrderState.CLOSED);
        if (entityEventDto != null) {
          entityEventDto.setMsisdn(orderDto.getMsisdn());
          entityEventDto.setExtRefId(String.valueOf(orderDto.getOrderId()));
          messageService.fireCashoutEvent(entityEventDto, AmqpConfig.CASHOUT_ROUTING_KEY);
        }
        break;
      }
      default: {
        log.info("no need to update loans statuses");
      }
    }
  }

  private List<InformationTheAgreement> getClientContracts(String iin) {
    ResultResponsegetUserContractsList contractsList =
        siteExchangePortType.getUserContractsList(iin);

    if (Objects.nonNull(contractsList) && Objects.nonNull(contractsList.getData())) {
      return contractsList.getData();
    }
    return new ArrayList<>();
  }

  @RabbitListener(
      queues = {AmqpConfig.LOAN_PAYMENT_QUEUE_NAME},
      containerFactory = "jsaFactory",
      group = "loan-api"
  )
  @Transactional
  public void processLoanPayment(LoanWsPaymentDto paymentDto) {
    try {
      PaymentApp app = mapper.map(paymentDto.getPaymentApp(), PaymentApp.class);

      app.setContractDate(DatatypeFactory.newInstance()
          .newXMLGregorianCalendar(paymentDto.getContractDate()));
      app.setTxnDate(DatatypeFactory.newDefaultInstance()
          .newXMLGregorianCalendar(String.valueOf(LocalDateTime.now())));
      app.setCommand(LoanPaymentConstants.TYPE_PAY);
      Response response = servicesPortType.payment(app);
      if (Objects.nonNull(response) && Objects.nonNull(response.getResult())) {
        if (response.getResult().equals(SUCCESS_RESPONSE_RESULT)) {
          paymentService.updateProcessingInfo(paymentDto.getPaymentId(),
              LoanPaymentResponseDto.builder()
                  .paymentTime(LocalDateTime.now())
                  .paymentId(paymentDto.getPaymentId())
                  .status(LoanPaymentStatus.SUCCESS)
                  .message(response.getComment())
                  .build());
          substractLoanRemainAmount(paymentDto.getPaymentId(), paymentDto.getPaymentApp().getSum());
        } else {
          log.info("Error processing loan payment paymentId: {}, result: {}, message: {}",
              paymentDto.getPaymentId(), response.getResult(), response.getComment());
          paymentService
              .updateProcessingInfo(paymentDto.getPaymentId(),
                  errorResponseDto(paymentDto.getPaymentId(), response.getComment()));
          telegramBotController
              .sendLoanPaymentErrorNotification(createPaymentErrorDto(paymentDto.getPaymentId(),
                  response.getComment()));
        }
      } else {
        log.info("Error processing loan payment paymentId: {}", paymentDto.getPaymentId());
        paymentService
            .updateProcessingInfo(paymentDto.getPaymentId(),
                errorResponseDto(paymentDto.getPaymentId(), "Error"));
        telegramBotController
            .sendLoanPaymentErrorNotification(createPaymentErrorDto(paymentDto.getPaymentId(),
                "Error"));
      }
    } catch (Exception e) {
      log.info("Error during processing "
          + "loan payment paymentId: {}, error: {}", paymentDto.getPaymentId(), e.getMessage());
      telegramBotController
          .sendLoanPaymentErrorNotification(createPaymentErrorDto(paymentDto.getPaymentId(),
              e.getMessage()));
    }
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

  private List<OrderDto> getClientOpenLoans(String iin) {
    return loanOrdersRepository
        .findAllByIinAndStatusIn(iin, Arrays
            .asList(OrderState.CASHED_OUT_CARD, OrderState.CASHED_OUT_WALLET))
        .stream()
        .map(o -> mapper.map(o, OrderDto.class))
        .collect(Collectors.toList());
  }

  private LoanPaymentResponseDto errorResponseDto(Integer paymentId, String message) {
    return LoanPaymentResponseDto.builder()
        .paymentTime(LocalDateTime.now())
        .paymentId(paymentId)
        .status(LoanPaymentStatus.ERROR)
        .message(message)
        .build();
  }

  private LoanPaymentErrorDto createPaymentErrorDto(Integer paymentId, String message) {
    return LoanPaymentErrorDto.builder()
        .paymentId(paymentId)
        .comment(message)
        .build();
  }

  private void updateLoanPayoutOrderState(Integer orderId, AcquiringOrderState orderState,
      String body) {
    if (AcquiringOrderState.SUCCESS.equals(orderState)) {
      loanOrdersService.updateLoanOrderPayoutResponseInfo(
          orderId,
          OrderState.CASHED_OUT_CARD,
          body
      );
    } else if (LoanPaymentConstants.ACQUIRING_ERROR_STATUSES
        .contains(orderState)) {
      loanOrdersService.updateLoanOrderPayoutResponseInfo(
          orderId,
          OrderState.CASH_OUT_CARD_FAILED,
          body
      );
    }
  }
}
