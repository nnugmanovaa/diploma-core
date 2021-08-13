package kz.codesmith.epay.loan.api.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.model.halyk.HalyCallbackRequestDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardCashoutResponseDto;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ILoanOrdersService {

  Page<OrderDto> getOrdersByUserOwner(
      LocalDate startDate,
      LocalDate endDate,
      Integer orderId,
      Pageable pageRequest
  );

  OrderDto getOrderByUserOwner(Integer orderId);

  OrderDto createNewPrimaryLoanOrder(ScoringRequest request);

  List<AlternativeChoiceDto> createNewAlternativeLoanOrders(
      OrderDto order,
      List<AlternativeChoiceDto> alternatives
  );

  OrderDto updateLoanOrderStatus(Integer orderId, OrderState status);

  OrderDto updateLoanOrderStatusAndLoanEffectiveRate(
      Integer orderId,
      OrderState status,
      BigDecimal loanEffectiveRate
  );

  OrderDto rejectLoanOrder(Integer orderId, String rejectReason);

  OrderDto rejectLoanOrder(Integer orderId, OrderState status, String rejectReason);

  OrderDto updateLoanOrderExtRefs(
      Integer orderId,
      String orderExtRefId,
      LocalDateTime orderExtRefTime
  );

  OrderDto updateLoanOrderContractRefs(
      Integer orderId,
      String contractDocumentS3Key,
      String contractExtRefId,
      LocalDateTime contractExtRefTime
  );

  OrderDto updateLoanOrderCashoutCardInitInfo(
      Integer orderId,
      OrderState status,
      HalykCardCashoutResponseDto halykCardTransfer
  );

  OrderDto updateLoanOrderCashoutCardBankResponseInfo(
      Integer orderId,
      OrderState status,
      HalyCallbackRequestDto request
  );

  OrderDto approveOrder(Integer orderId);

  List<AlternativeChoiceDto> getAlternativeChoices(OrderDto order);

  OrderDto updateScoringInfo(
      Integer orderId,
      ScoringInfo scoringInfo
  );

  byte[] getOrderContractDocument(Integer orderId);

  byte[] getLoanDebtorFormPdf(Integer orderId);
}
