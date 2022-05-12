package kz.codesmith.epay.loan.api.diploma.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.ScoringProperties;
import kz.codesmith.epay.loan.api.diploma.component.acquiring.AcquiringProperties;
import kz.codesmith.epay.loan.api.diploma.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.diploma.model.LoanInfoDto;
import kz.codesmith.epay.loan.api.diploma.model.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.OrderInitDto;
import kz.codesmith.epay.loan.api.diploma.model.ScoringModel;
import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.diploma.service.IMessageService;
import kz.codesmith.epay.loan.api.diploma.service.IPayoutService;
import kz.codesmith.epay.loan.api.diploma.service.IPkbConnectorService;
import kz.codesmith.epay.loan.api.diploma.service.IScoringService;
import kz.codesmith.epay.loan.api.domain.clients.ClientEntity;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentDto;
import kz.codesmith.epay.loan.api.model.exception.AcquiringProcessingException;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderType;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.repository.ClientsRepository;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.repository.ScoringVarsRepository;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.payintech.ListLoanMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements IScoringService {
  private final IPkbConnectorService pkbConnectorService;
  private final IScoreVariablesService scoreVariablesService;
  private final ScoringProperties scoringProperties;
  private final ScoringContext scoringContext;
  private final ScoringVarsRepository repository;
  private final ObjectMapper objectMapper;
  private final UserContextHolder contextHolder;
  private final LoanOrdersRepository ordersRepository;
  private final IPayoutService payoutService;
  private final AcquiringProperties acquiringProperties;
  private final PaymentRepository paymentRepository;
  private final AcquiringRs acquiringRs;
  private final IMessageService messageService;

  @Override
  public ScoringResponse score(ScoringRequest request) {
    OrderEntity orderEntity = new OrderEntity();
    orderEntity.setStatus(OrderState.NEW);
    orderEntity.setLoanProduct("1-0000007");
    orderEntity.setMsisdn(contextHolder.getContext().getUsername());
    orderEntity.setIin(request.getIin());
    orderEntity.setLoanAmount(BigDecimal.valueOf(request.getLoanAmount()));
    orderEntity.setLoanMethod(ListLoanMethod.ANNUITY_PAYMENTS.value());
    orderEntity.setClientId(contextHolder.getContext().getOwnerId());
    orderEntity.setClientInfo(getClientFullName(request.getPersonalInfo()));
    orderEntity.setLoanPeriodMonths(request.getLoanPeriod());
    orderEntity.setOrderType(OrderType.PRIMARY);
    orderEntity = ordersRepository.save(orderEntity);

    ScoringModel scoringModel = pkbConnectorService.getScoringModelByIin(request.getIin());
    scoringModel.setLoanAmount(BigDecimal.valueOf(request.getLoanAmount()));
    scoringModel.setPeriod(request.getLoanPeriod());
    scoringModel.setNumberOfKids(request.getPersonalInfo().getNumberOfKids());

    scoringContext.setVariablesHolder(
        new VariablesHolder(
            getScoringVarsMap(),
            objectMapper
        )
    );

    List<OrderEntity> orders = ordersRepository
        .findAllByIinAndStatusIn(request.getIin(),
            Collections.singletonList(OrderState.CASHED_OUT_CARD));
    if (!CollectionUtils.isEmpty(orders)) {
      orderEntity.setStatus(OrderState.REJECTED);
      ordersRepository.save(orderEntity);
      return ScoringResponse.builder()
          .orderId(orderEntity.getOrderId())
          .result(ScoringResult.REFUSED)
          .rejectText("Need to repay issued loans")
          .scoringInfo(scoringModel)
          .orderTime(LocalDateTime.now())
          .build();
    }

    if (scoringModel.getKdn() > scoringProperties.getMaxKdn()) {
      orderEntity.setStatus(OrderState.REJECTED);
      ordersRepository.save(orderEntity);
      return ScoringResponse.builder()
          .orderId(orderEntity.getOrderId())
          .result(ScoringResult.REFUSED)
          .scoringInfo(scoringModel)
          .orderTime(LocalDateTime.now())
          .build();
    }

    var numberOfActiveLoans = scoreVariablesService
        .getValue(ScoringVars.NUMBER_OF_ACTIVE_LOANS, Double.class);
    var income = scoreVariablesService
        .getValue(ScoringVars.INCOME, Double.class);
    var debt = scoreVariablesService
        .getValue(ScoringVars.DEBT, Double.class);
    var maxDelayInDays = scoreVariablesService
        .getValue(ScoringVars.MAX_DELAY_IN_DAYS, Double.class);
    var totalNumberOfDelays = scoreVariablesService
        .getValue(ScoringVars.TOTAL_NUMBER_OF_DELAYS, Double.class);
    var maxOverdueAmount = scoreVariablesService
        .getValue(ScoringVars.MAX_OVERDUE_AMOUNT, Double.class);
    var loanAmount = scoreVariablesService
        .getValue(ScoringVars.LOAN_AMOUNT, Double.class);
    var loanPeriod = scoreVariablesService
        .getValue(ScoringVars.PERIOD, Double.class);
    var numberOfKids = scoreVariablesService
        .getValue(ScoringVars.NUMBER_OF_KIDS, Double.class);

    var score = scoringModel.getNumberOfActiveLoans() * numberOfActiveLoans
        + income * scoringModel.getIncome()
        + debt * scoringModel.getDebt()
        + maxDelayInDays * scoringModel.getMaxOverdueDays()
        + totalNumberOfDelays * scoringModel.getOverduesSum()
        + maxOverdueAmount * scoringModel.getMaxOverdueAmount()
        + loanAmount * scoringModel.getLoanAmount().doubleValue()
        + loanPeriod * scoringModel.getPeriod()
        + numberOfKids * scoringModel.getNumberOfKids();

    if (score > scoringProperties.getMaxScoringResult()) {
      orderEntity.setStatus(OrderState.REJECTED);
      ordersRepository.save(orderEntity);
      return ScoringResponse.builder()
          .result(ScoringResult.REFUSED)
          .scoringInfo(scoringModel)
          .orderTime(LocalDateTime.now())
          .build();
    }
    orderEntity.setStatus(OrderState.APPROVED);
    orderEntity = ordersRepository.save(orderEntity);
    return ScoringResponse.builder()
        .orderId(orderEntity.getOrderId())
        .result(ScoringResult.APPROVED)
        .scoringInfo(scoringModel)
        .orderTime(LocalDateTime.now())
        .build();
  }

  @Override
  public LoanCheckAccountResponse getAccountLoans(LoanCheckAccountRequestDto requestDto) {
    Optional<OrderEntity> orderEntity = ordersRepository
        .findByIinAndStatus(requestDto.getIin(), OrderState.CASHED_OUT_CARD);
    if (orderEntity.isPresent()) {
      OrderEntity order = orderEntity.get();
      return LoanCheckAccountResponse.builder()
          .message("Активные займы")
          .loans(Collections.singletonList(LoanInfoDto.builder()
                  .amountOfDebt(order.getRestAmount())
                  .contractDate(String.valueOf(order.getContractExtRefTime().toLocalDate()))
                  .contractNumber(order.getContractExtRefId())
                  .loanPeriodMonths(order.getRestPeriod())
                  .plannedPaymentAmount(order
                      .getRestAmount()
                      .divide(BigDecimal
                          .valueOf(order.getRestPeriod()),4, RoundingMode.HALF_UP))
              .build()))
          .build();
    }
    return LoanCheckAccountResponse.builder()
        .message("Не удалось получить список активных займов")
        .loans(new ArrayList<>())
        .build();
  }

  @Override
  public AcquiringPaymentResponse initPayment(LoanPaymentRequestDto requestDto) {
    OrderEntity order = ordersRepository.findByIinAndStatus(requestDto.getClientRef(),
        OrderState.CASHED_OUT_CARD).get();
    requestDto.setOrderId(order.getOrderId());
    PaymentEntity paymentEntity = startNewPayment(toOrderInitDto(requestDto));
    AcquiringPaymentResponse outPaymentResponse = AcquiringPaymentResponse.builder()
        .operationTime(LocalDateTime.now())
        .build();

    try {
      PaymentDto paymentDto = buildPaymentDto(paymentEntity);
      Response response = acquiringRs.init(paymentDto);

      if (response.getStatus() == HttpStatus.OK.value()) {
        OrderSummaryDto paymentInitResponse = objectMapper
            .readValue(response.readEntity(String.class), OrderSummaryDto.class);

        if (paymentInitResponse.getState().equals(AcquiringOrderState.NEW)) {
          outPaymentResponse.setStatus(AcquiringBaseStatus.NEW);
          outPaymentResponse.setUrl(paymentInitResponse.getPaymentUrl());

          messageService.fireLoanStatusGetEvent(PayoutUpdateStateEventDto.builder()
                  .paymentId(paymentEntity.getPaymentId())
                  .loanOrderId(requestDto.getOrderId())
                  .extRefId(paymentInitResponse.getOrdersId())
                  .extUuid(paymentInitResponse.getUuid())
                  .extRefTime(paymentInitResponse.getOrderDate())
                  .action("payment")
                  .build(),
              AmqpConfig.LOAN_STATUSES_ROUTING_KEY);
        } else {
          outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
        }
        outPaymentResponse.setMessage(paymentInitResponse.getMessage());
        paymentEntity.setExtRefId(paymentInitResponse.getOrdersId());
        paymentEntity.setExtRefTime(paymentInitResponse.getOrderDate().atStartOfDay());
        paymentEntity.setExtUuid(paymentInitResponse.getUuid());
      } else {
        outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
      }

      paymentEntity.setInitPaymentResponse(
          objectMapper.convertValue(
              outPaymentResponse,
              new TypeReference<>() {
              }
          ));
    } catch (Exception ex) {
      outPaymentResponse.setMessage(ex.getMessage());
      outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
    }

    paymentEntity.setInitPaymentStatus(outPaymentResponse.getStatus());
    paymentRepository.save(paymentEntity);
    return outPaymentResponse;
  }

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
            if (order.getRestAmount().intValue() == orderSummaryDto.getAmount().intValue()) {
              order.setRestPeriod(0);
              order.setRestAmount(BigDecimal.ZERO);
              order.setStatus(OrderState.CLOSED);
            } else {
              BigDecimal restAmount = order.getLoanAmount()
                  .subtract(BigDecimal.valueOf(orderSummaryDto.getAmount().intValue()));
              order.setRestAmount(restAmount);
              order.setRestPeriod(order.getRestPeriod()-1);
              if (restAmount.intValue() == 0) {
                order.setStatus(OrderState.CLOSED);
              }
            }
            ordersRepository.save(order);
            return orderSummaryDto;
          }  else {
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

  private OrderInitDto toOrderInitDto(LoanPaymentRequestDto requestDto) {
    return OrderInitDto.builder()
        .loanRepayType(requestDto.getLoanRepayType())
        .productExtRefId(requestDto.getLoanRepayType().toString())
        .amount(requestDto.getAmount())
        .contractDate(requestDto.getContractDate())
        .orderTime(LocalDateTime.now())
        .contractNumber(requestDto.getContractNumber())
        .iin(requestDto.getClientRef())
        .orderId(requestDto.getOrderId())
        .build();
  }

  public Map<String, String> getScoringVarsMap() {
    Map<String, String> scoringVars = new HashMap<>();
    repository.findAll()
        .forEach(r -> scoringVars.put(r.getConstantName(), r.getValue()));
    return scoringVars;
  }

  private String getClientFullName(PersonalInfoDto dto) {
    return String.format("%s %s", dto.getLastName(), dto.getFirstName());
  }

  private PaymentEntity startNewPayment(OrderInitDto orderDto) {
    PaymentEntity payment = new PaymentEntity();
    payment.setAmount(orderDto.getAmount());
    payment.setClientRef(orderDto.getIin());
    payment.setContractDate(orderDto.getContractDate());
    payment.setContractNumber(orderDto.getContractNumber());
    payment.setDescription("Погашение микрокредита");
    payment.setCurrency("KZT");
    payment.setClientsId(contextHolder.getContext().getOwnerId());
    payment.setLoanOrderId(orderDto.getOrderId());
    return paymentRepository.save(payment);
  }

  private PaymentDto buildPaymentDto(PaymentEntity payment) {
    int orderId = payment.getPaymentId();

    return PaymentDto.builder()
        .amount(payment
            .getAmount()
            .setScale(2, RoundingMode.HALF_UP)
            .doubleValue())
        .extClientRef(payment.getClientRef())
        .currency(payment.getCurrency())
        .extOrdersId(String.valueOf(orderId))
        .description(payment.getDescription())
        .successReturnUrl(acquiringProperties.getSuccessReturnUrl())
        .errorReturnUrl(acquiringProperties.getErrorReturnUrl())
        .callbackSuccessUrl(acquiringProperties.getCallbackSuccessUrl())
        .callbackErrorUrl(acquiringProperties.getCallbackErrorUrl())
        .payload(Collections.emptyMap())
        .build();
  }
}
