package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.acquiring.AcquiringProperties;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringCardSaveDto;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationRequest;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringStatusResponse;
import kz.codesmith.epay.loan.api.model.acquiring.CardSaveDto;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentCallbackEventDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentDto;
import kz.codesmith.epay.loan.api.model.acquiring.SaveCardRequestDto;
import kz.codesmith.epay.loan.api.model.exception.AcquiringProcessingException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.payment.LoanPaymentConstants;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcquiringService implements IAcquiringService {

  private final PaymentRepository paymentRepository;
  private final AcquiringProperties acquiringProperties;
  private final IMessageService messageService;
  private final IPaymentService paymentService;
  private final AcquiringRs acquiringRs;
  private final ObjectMapper objectMapper;
  private final ModelMapper modelMapper;

  @Override
  public AcquiringPaymentResponse initPayment(OrderInitDto orderDto) {
    PaymentEntity paymentEntity = paymentService.startNewPayment(orderDto);
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
        } else {
          outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
        }
        outPaymentResponse.setMessage(paymentInitResponse.getMessage());
        paymentEntity.setExtRefId(paymentInitResponse.getOrdersId());
        paymentEntity.setExtRefTime(paymentInitResponse.getOrderDate().atStartOfDay());
        paymentEntity.setExtUuid(paymentInitResponse.getUuid());
      } else {
        outPaymentResponse.setMessage(LoanPaymentConstants.MESSAGE_UNEXPECTED_BEHAVIOUR);
        outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
      }

      paymentEntity.setInitPaymentResponse(
          objectMapper.convertValue(
              outPaymentResponse,
              new TypeReference<>() {
              }
          ));
    } catch (Exception ex) {
      log.error(LoanPaymentConstants.MESSAGE_DEFAULT_EXCEPTION, ex);
      outPaymentResponse.setMessage(ex.getMessage());
      outPaymentResponse.setStatus(AcquiringBaseStatus.ERROR);
    }

    paymentEntity.setInitPaymentStatus(outPaymentResponse.getStatus());
    paymentRepository.save(paymentEntity);
    return outPaymentResponse;
  }

  @Override
  public AcquiringStatusResponse getPaymentStatus(Integer orderId) {
    AcquiringStatusResponse outStatusResponse = AcquiringStatusResponse.builder()
        .operationTime(LocalDateTime.now())
        .build();

    PaymentEntity paymentEntity = paymentService.getPayment(orderId);
    String extRefId = paymentEntity.getExtRefId();
    String extUuid = paymentEntity.getExtUuid();
    LocalDateTime extRefTime = paymentEntity.getExtRefTime();

    try {
      if (Objects.nonNull(extRefId)
          && Objects.nonNull(extRefTime)
          && Objects.nonNull(extUuid)) {
        Response response = acquiringRs.getStatus(
            extRefId,
            extRefTime.toLocalDate(),
            extUuid
        );

        if (response.getStatus() == HttpStatus.OK.value()) {
          OrderSummaryDto paymentStatusResponse = objectMapper
              .readValue(response.readEntity(String.class), OrderSummaryDto.class);
          AcquiringOrderState orderState = paymentStatusResponse.getState();
          paymentService.updatePaymentState(orderId, orderState);

          if (orderState == AcquiringOrderState.SUCCESS) {
            outStatusResponse.setStatus(AcquiringBaseStatus.SUCCESS);
          } else if (LoanPaymentConstants.ACQUIRING_ERROR_STATUSES.contains(orderState)) {
            outStatusResponse.setStatus(AcquiringBaseStatus.ERROR);
          } else {
            outStatusResponse.setStatus(AcquiringBaseStatus.IN_PROCESS);
          }
          outStatusResponse.setMessage(orderState.getDescription());
        }
      } else {
        log.error("Could not call acquiring get payment status, one of the params is null: "
            + "extRefId:{}, extRefTime: {}, extUuid: {}", extRefId, extRefTime, extUuid);
        outStatusResponse.setMessage(LoanPaymentConstants.MESSAGE_UNEXPECTED_BEHAVIOUR);
        outStatusResponse.setStatus(AcquiringBaseStatus.ERROR);
      }
    } catch (Exception ex) {
      log.error("Error during acquring payment status call", ex);
      outStatusResponse.setMessage(ex.getMessage());
      outStatusResponse.setStatus(AcquiringBaseStatus.ERROR);
    }

    return outStatusResponse;
  }

  @Override
  public OrderSummaryDto getOrderStatus(String extOrderId, String uuid, LocalDate extRefTime) {
    try {
      if (Objects.nonNull(extOrderId) && Objects.nonNull(uuid) && Objects.nonNull(extRefTime)) {
        Response response = acquiringRs.getStatus(extOrderId, extRefTime, uuid);
        if (response.getStatus() == 200) {
          return objectMapper
              .readValue(response.readEntity(String.class), OrderSummaryDto.class);
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

  @Override
  public AcquiringNotificationResponse processNotification(
      AcquiringNotificationRequest notificationRequest) {
    AcquiringNotificationResponse outNotificationResponse = AcquiringNotificationResponse.builder()
        .operationTime(LocalDateTime.now())
        .build();

    try {
      PaymentCallbackEventDto orderDto = objectMapper
          .readValue(notificationRequest.getBody(), PaymentCallbackEventDto.class);
      Integer orderId = Integer.valueOf(orderDto.getExtOrdersId());
      PaymentEntity paymentEntity = paymentService.getPayment(orderId);

      outNotificationResponse.setData(orderDto);
      outNotificationResponse.setMessage(orderDto.getMessage());

      if (paymentEntity.getOrderState() == null) {
        AcquiringStatusResponse paymentStatusResponse = getPaymentStatus(orderId);

        if (paymentStatusResponse.getStatus() == AcquiringBaseStatus.SUCCESS) {
          paymentEntity.setInitPaymentStatus(AcquiringBaseStatus.SUCCESS);
          messageService
              .firePayEvent(modelMapper.map(paymentEntity, LoanPaymentRequestDto.class),
                  AmqpConfig.PAYMENT_SEND_ROUTING_KEY);

          outNotificationResponse.setStatus(AcquiringBaseStatus.SUCCESS);
          paymentRepository.save(paymentEntity);
        } else {
          outNotificationResponse.setStatus(paymentStatusResponse.getStatus());
          outNotificationResponse.setMessage(LoanPaymentConstants.MESSAGE_BAD_STATUS_RESPONSE);
        }
      } else {
        outNotificationResponse.setStatus(AcquiringBaseStatus.ERROR);
        outNotificationResponse
            .setMessage(LoanPaymentConstants.MESSAGE_PAYMENT_ALREADY_PROCESSED);
      }
    } catch (NotFoundApiServerException nfex) {
      throw nfex;
    } catch (Exception ex) {
      log.error(LoanPaymentConstants.MESSAGE_DEFAULT_EXCEPTION, ex);
      outNotificationResponse.setMessage(ex.getMessage());
      outNotificationResponse.setStatus(AcquiringBaseStatus.ERROR);
    }

    return outNotificationResponse;
  }

  @Override
  public OrderSummaryDto saveCard(SaveCardRequestDto requestDto) {
    AcquiringCardSaveDto cardSaveDto = AcquiringCardSaveDto.builder()
        .amount(requestDto.getAmount())
        .currency(LoanPaymentConstants.KZT_PAYMENT_CURRENCY)
        .errorReturnUrl(requestDto.getErrorReturnUrl())
        .successReturnUrl(requestDto.getSuccessReturnUrl())
        .template(requestDto.getTemplate())
        .extClientRef(requestDto.getClient())
        .build();

    try {
      Response response = acquiringRs.saveCard(cardSaveDto);
      if (response.getStatus() == 200) {
        return objectMapper
            .readValue(response.readEntity(String.class), OrderSummaryDto.class);
      }
      log.error("Acquiring card save response status: {}", response.getStatus());
    } catch (Exception e) {
      log.error("Error during acquiring card save call", e);
      throw new AcquiringProcessingException(e.getMessage());
    }
    throw new GeneralApiServerException(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  @Override
  public List<CardSaveDto> getAllSavedCards(String extClientRef) {
    try {
      Response response = acquiringRs.getAllSavedCards(extClientRef);
      if (response.getStatus() == 200) {
        return objectMapper.readValue(response.readEntity(String.class), List.class);
      }
      log.error("Acquiring get all saved cards response status: {}", response.getStatus());
    } catch (Exception e) {
      log.error("Error during acquiring card save call", e);
      throw new MfoGeneralApiException();
    }
    throw new GeneralApiServerException(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  @Override
  public void deleteSavedCard(String cardsId) {
    try {
      Response response = acquiringRs.deleteSavedCard(cardsId);
      log.error("Acquiring delete saved response status: {}", response.getStatus());
      if (response.getStatus() != 200) {
        throw new AcquiringProcessingException("Error processing DELETE CARD request");
      }
    } catch (Exception e) {
      log.error("Error during acquiring delete saved card call", e);
      throw new MfoGeneralApiException();
    }
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
        .successReturnUrl(acquiringProperties.getSuccessReturnUrl() + orderId)
        .errorReturnUrl(acquiringProperties.getErrorReturnUrl() + orderId)
        .payload(Collections.emptyMap())
        .build();
  }
}
