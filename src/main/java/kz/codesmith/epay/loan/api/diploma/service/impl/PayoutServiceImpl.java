package kz.codesmith.epay.loan.api.diploma.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.payout.PayoutProperties;
import kz.codesmith.epay.loan.api.diploma.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutResponseDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.diploma.service.IMessageService;
import kz.codesmith.epay.loan.api.diploma.service.IOrdersService;
import kz.codesmith.epay.loan.api.diploma.service.IPayoutService;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.exception.AcquiringProcessingException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayoutServiceImpl implements IPayoutService {
  private final IOrdersService loanOrdersService;
  private final AcquiringRs acquiringRs;
  private final PayoutProperties payoutProperties;
  private final ObjectMapper objectMapper;
  private final UserContextHolder userContextHolder;
  private final PaymentRepository paymentRepository;
  private final LoanOrdersRepository ordersRepository;
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
      messageService.fireLoanStatusGetEvent(PayoutUpdateStateEventDto.builder()
              .paymentId(paymentEntity.getPaymentId())
              .loanOrderId(request.getOrderId())
              .extRefId(payoutResponseDto.getOrdersId())
              .extUuid(payoutResponseDto.getUuid())
              .extRefTime(payoutResponseDto.getOrderDate())
              .action("payout")
              .build(),
          AmqpConfig.LOAN_STATUSES_ROUTING_KEY);
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
  public OrderSummaryDto getOrderStatus(PayoutUpdateStateEventDto updateStateEventDto) {
    try {
      String extOrderId = updateStateEventDto.getExtRefId();
      String uuid = updateStateEventDto.getExtUuid();
      LocalDate extRefTime = updateStateEventDto.getExtRefTime();
      if (Objects.nonNull(extOrderId) && Objects.nonNull(uuid) && Objects.nonNull(extRefTime)) {
        Response response = acquiringRs.getStatus(extOrderId, extRefTime, uuid);
        if (response.getStatus() == 200) {
          OrderSummaryDto orderSummaryDto = objectMapper
              .readValue(response.readEntity(String.class), OrderSummaryDto.class);
          OrderEntity order = ordersRepository
              .findByOrderId(updateStateEventDto.getLoanOrderId()).get();
          if (orderSummaryDto.getState().equals(AcquiringOrderState.SUCCESS)) {
            order.setStatus(OrderState.CASHED_OUT_CARD);
            order.setContractExtRefTime(LocalDateTime.now());
            order.setContractExtRefId(String.valueOf(RandomUtils.nextLong()));
            order.setRestPeriod(order.getLoanPeriodMonths());
            order.setRestAmount(order.getLoanAmount());
            return orderSummaryDto;
          } else if (orderSummaryDto.getState().equals(AcquiringOrderState.NEW)){
            order.setStatus(OrderState.APPROVED);
            return orderSummaryDto;
          } else {
            order.setStatus(OrderState.CASH_OUT_CARD_FAILED);
            return orderSummaryDto;
          }
        }
      }
      log.error("Could not call acquiring get payment status, one of the params is null: "
          + "extRefId:{}, extRefTime: {}, extUuid: {}", extOrderId, extRefTime, uuid);
    } catch (Exception e) {
      log.error("Error during acquring payment status call", e);
      throw new AcquiringProcessingException(e.getMessage());
    }
    throw new GeneralApiServerException(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  private PaymentEntity startNewPayment(OrderEntity orderDto) {
    PaymentEntity paymentEntity = new PaymentEntity();
    paymentEntity.setLoanOrderId(orderDto.getOrderId());
    paymentEntity.setAmount(orderDto.getLoanAmount());
    paymentEntity.setClientRef(userContextHolder.getContext().getUsername());
    paymentEntity.setCurrency(PAYOUT_CURRENCY_KZT);
    paymentEntity.setDescription(PAYOUT_DESCRIPTION_MESSAGE);
    paymentEntity.setOrderState(AcquiringOrderState.NEW);
    paymentEntity.setContractDate(String.valueOf(LocalDate.now()));
    paymentEntity.setContractNumber(String.valueOf(RandomUtils.nextLong()));
    paymentRepository.save(paymentEntity);
    return paymentEntity;
  }

  private PayoutRequestDto buildPayoutRequestDto(PayoutToCardRequestDto request, OrderEntity order) {
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
}
