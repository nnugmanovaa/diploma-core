package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.acquiring.AcquiringProperties;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationRequest;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringNotificationResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringStatusResponse;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentCallbackEventDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentDto;
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
  private final IMessageService acquiringMessageService;
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

        outPaymentResponse.setMessage(paymentInitResponse.getMessage());
        outPaymentResponse.setStatus(AcquiringBaseStatus.SUCCESS);
        outPaymentResponse.setUrl(paymentInitResponse.getPaymentUrl());

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
        outStatusResponse.setMessage(LoanPaymentConstants.MESSAGE_UNEXPECTED_BEHAVIOUR);
        outStatusResponse.setStatus(AcquiringBaseStatus.ERROR);
      }
    } catch (Exception ex) {
      log.error(LoanPaymentConstants.MESSAGE_DEFAULT_EXCEPTION, ex);
      outStatusResponse.setMessage(ex.getMessage());
      outStatusResponse.setStatus(AcquiringBaseStatus.ERROR);
    }

    return outStatusResponse;
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
          acquiringMessageService
              .firePayEvent(modelMapper.map(paymentEntity, LoanPaymentRequestDto.class),
                  AmqpConfig.PAYMENT_SEND_ROUTING_KEY);

          outNotificationResponse.setStatus(AcquiringBaseStatus.SUCCESS);
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
