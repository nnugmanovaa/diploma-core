package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.payout.PayoutProperties;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentCallbackEventDto;
import kz.codesmith.epay.loan.api.model.exception.AcquiringProcessingException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.payout.MfoPayoutDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutResponseDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.payment.LoanRepayType;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.epay.loan.api.service.IPayoutService;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayoutServiceImpl implements IPayoutService {
  private final ILoanOrdersService loanOrdersService;
  private final IAcquiringService acquiringService;
  private final AcquiringRs acquiringRs;
  private final PayoutProperties payoutProperties;
  private final ObjectMapper objectMapper;
  private final UserContextHolder userContextHolder;
  private final PaymentRepository paymentRepository;
  private final IMessageService messageService;

  private static final String PAYOUT_DESCRIPTION_MESSAGE = "Выплата на карту";
  private static final String PAYOUT_CURRENCY_KZT = "KZT";

  @Transactional
  @Override
  public PayoutToCardResponseDto initPayoutToCard(PayoutToCardRequestDto request) {
    var order = loanOrdersService.getOrderByUserOwner(request.getOrderId());
    if (Objects.isNull(order)) {
      throw new NotFoundApiServerException(
          ApiErrorTypeParamValues.ORDER,
          request.getOrderId()
      );
    }
    if (!initPayoutStates.contains(order.getStatus())) {
      log.error("Improper order state for payout: {}", order.getStatus());
      throw new MfoGeneralApiException("Improper order status " + order.getStatus());
    }
    PaymentEntity paymentEntity = startNewPayment(order);
    PayoutRequestDto payoutRequestDto = buildPayoutRequestDto(request, order);
    if (!payoutProperties.getEnabled()) {
      paymentEntity.setExtRefTime(LocalDateTime.now());
      sendAsEvent(order.getOrderId(), AcquiringOrderState.SUCCESS, null);
      return fillFakeInitResponse(order, paymentEntity);
    }
    try {
      Response response = acquiringRs.payout(payoutRequestDto);
      if (response.getStatus() != 200) {
        return PayoutToCardResponseDto.builder()
            .orderState(AcquiringOrderState.ERROR)
            .orderId(order.getOrderId())
            .amount(order.getLoanAmount())
            .build();
      }
      PayoutResponseDto payoutResponseDto = objectMapper
          .readValue(response.readEntity(String.class), PayoutResponseDto.class);
      paymentEntity.setExtUuid(payoutResponseDto.getUuid());
      paymentEntity.setExtRefTime(payoutResponseDto.getOrderDate().atStartOfDay());
      paymentEntity.setExtRefId(payoutResponseDto.getOrdersId());
      paymentEntity.setOrderState(payoutResponseDto.getState());
      paymentEntity.setInitPaymentResponse(objectMapper.convertValue(
          payoutResponseDto,
          new TypeReference<>() {
          }
      ));
      sendAsEvent(order.getOrderId(),payoutResponseDto.getState(), null);
      return PayoutToCardResponseDto.builder()
          .httpMethod(HttpMethod.GET.name())
          .amount(order.getLoanAmount())
          .orderId(order.getOrderId())
          .orderState(payoutResponseDto.getState())
          .url(payoutResponseDto.getPaymentUrl())
          .uuid(payoutResponseDto.getUuid())
          .build();
    } catch (Exception e) {
      log.info("Failed to init payout to card, {}", e.getMessage());
      throw new AcquiringProcessingException(e.getMessage());
    }
  }

  @Transactional
  @Override
  public void acceptPayoutCallbackRequest(String body) {
    try {
      PaymentCallbackEventDto callbackEventDto = objectMapper
          .readValue(body, PaymentCallbackEventDto.class);

      Integer loanOrderId = Integer.valueOf(callbackEventDto.getExtOrdersId());
      PaymentEntity paymentEntity = paymentRepository
          .findByLoanOrderId(loanOrderId)
          .orElseThrow(() ->
              new NotFoundApiServerException(ApiErrorTypeParamValues.ORDER, loanOrderId));

      OrderSummaryDto orderSummaryDto = checkStatusSummaryDto(paymentEntity);
      paymentEntity.setOrderState(orderSummaryDto.getState());
      sendAsEvent(Integer.valueOf(callbackEventDto.getExtOrdersId()),
          orderSummaryDto.getState(),
          body);
    } catch (Exception e) {
      log.error("Exception accepting PAYOUT callback: ", e);
    }
  }

  private OrderSummaryDto checkStatusSummaryDto(PaymentEntity paymentEntity) {
    try {
      return acquiringService
          .getOrderStatus(paymentEntity.getExtRefId(),
              paymentEntity.getExtUuid(),
              paymentEntity.getExtRefTime().toLocalDate());
    } catch (Exception e) {
      log.error("Error during payout order status check: {}", e.getMessage());
      throw new AcquiringProcessingException(e.getMessage());
    }
  }

  @Override
  public void checkPayoutStatuses() {
    List<Integer> ordersId = loanOrdersService
        .getAllCashedOutInitializedOrders()
        .stream()
        .map(OrderDto::getOrderId)
        .collect(Collectors.toList());

    if (!CollectionUtils.isEmpty(ordersId)) {
      List<PayoutUpdateStateEventDto> entities = paymentRepository
          .findAllByLoanOrderIdIn(ordersId)
          .stream()
          .map(this::mapToEventDto)
          .collect(Collectors.toList());

      if (!CollectionUtils.isEmpty(entities)) {
        entities.forEach(entity -> {
          messageService.firePayoutStatusUpdateEvent(entity, AmqpConfig.PAYOUT_ROUTING_KEY);
        });
      }
    }
  }

  private PayoutUpdateStateEventDto mapToEventDto(PaymentEntity entity) {
    return PayoutUpdateStateEventDto.builder()
        .paymentId(entity.getPaymentId())
        .loanOrderId(entity.getLoanOrderId())
        .extRefId(entity.getExtRefId())
        .extUuid(entity.getExtUuid())
        .extRefTime(entity.getExtRefTime().toLocalDate())
        .build();
  }

  private PaymentEntity startNewPayment(OrderDto orderDto) {
    PaymentEntity paymentEntity = new PaymentEntity();
    paymentEntity.setLoanOrderId(orderDto.getOrderId());
    paymentEntity.setAmount(orderDto.getLoanAmount());
    paymentEntity.setClientRef(userContextHolder.getContext().getUsername());
    paymentEntity.setCurrency(PAYOUT_CURRENCY_KZT);
    paymentEntity.setDescription(PAYOUT_DESCRIPTION_MESSAGE);
    paymentEntity.setLoanRepayType(LoanRepayType.PAYOUT);
    paymentEntity.setOrderState(AcquiringOrderState.NEW);
    paymentEntity.setContractDate(String.valueOf(orderDto
        .getContractExtRefTime()
        .toLocalDate()));
    paymentEntity.setContractNumber(orderDto.getContractExtRefId());
    paymentRepository.save(paymentEntity);
    return paymentEntity;
  }

  private PayoutRequestDto buildPayoutRequestDto(PayoutToCardRequestDto request, OrderDto order) {
    log.info("callbackSuccessUrl: {}, callbackErrorUrl: {}",
        payoutProperties.getCallbackSuccessUrl(), payoutProperties.getCallbackErrorUrl());
    return PayoutRequestDto.builder()
        .amount(order.getLoanAmount().doubleValue())
        .cardsId(payoutProperties.getCardIdFrom())
        .toCardsId(request.getToCardsId())
        .currency(PAYOUT_CURRENCY_KZT)
        .description(PAYOUT_DESCRIPTION_MESSAGE)
        .extOrdersId(String.valueOf(order.getOrderId()))
        .successReturnUrl(request.getBackSuccessLink())
        .errorReturnUrl(request.getBackFailureLink())
        .extClientRef(userContextHolder.getContext().getUsername())
        .callbackSuccessUrl(payoutProperties.getCallbackSuccessUrl())
        .callbackErrorUrl(payoutProperties.getCallbackErrorUrl())
        .build();
  }

  private PayoutToCardResponseDto fillFakeInitResponse(OrderDto order,
      PaymentEntity paymentEntity) {
    String uuid = "e0b16a9d-dccd-405c-b5bd-46425307e965";
    paymentEntity.setExtUuid(uuid);
    paymentEntity.setExtRefTime(LocalDateTime.of(2021, 11, 10,
        15, 26, 17));
    paymentEntity.setExtRefId("167112640259682");
    paymentEntity.setOrderState(AcquiringOrderState.SUCCESS);
    paymentRepository.save(paymentEntity);

    String successUrl = "https://cards-stage.pitech.kz/pay/order"
        + "/state?ordersId=061998074880761&"
        + "orderDate=2021-11-23&uuid=c55dc7bc-0575-4ab1-a06c-491d589f1656";
    return PayoutToCardResponseDto.builder()
        .httpMethod(HttpMethod.GET.name())
        .amount(order.getLoanAmount())
        .orderId(order.getOrderId())
        .orderState(AcquiringOrderState.SUCCESS)
        .url(successUrl)
        .uuid(uuid)
        .build();
  }

  private void sendAsEvent(Integer loanOrderId, AcquiringOrderState orderState, String body) {
    messageService.fireMfoPayoutStatusUpdateEvent(MfoPayoutDto.builder()
        .loanOrderId(loanOrderId)
        .orderState(orderState)
        .body(body)
        .build(), AmqpConfig.MFO_PAYOUT_ROUTING_KEY);
  }
}
