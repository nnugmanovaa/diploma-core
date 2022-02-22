package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import kz.codesmith.epay.loan.api.domain.OrderScoringVariables;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoUtils;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.requirement.ScoringRequirement;
import kz.codesmith.epay.loan.api.requirement.ScoringRequirementResult;
import kz.codesmith.epay.loan.api.service.IAlternativeLoanCalculationService;
import kz.codesmith.epay.loan.api.service.IClientsService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.epay.loan.api.service.IScoringService;
import kz.codesmith.epay.loan.api.service.StorageService;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.codesmith.logger.Logged;
import kz.payintech.LoanSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Slf4j
@Logged
@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements IScoringService {
  private final IClientsService clientsService;
  private final LoanService loanService;
  private final ILoanOrdersService ordersServices;
  private final ScoringContext context;
  private final ScoringVarsService scoringVarsService;
  private final ObjectMapper objectMapper;
  private final ScoringRequirement requirement;
  private final IMfoCoreService mfoCoreService;
  private final StorageService storageService;
  private final IAlternativeLoanCalculationService alternativeLoanCalculation;

  @Value("${scoring.iin.whitelist}")
  private String iinWhiteList;

  @Value("${scoring.iin.backlist}")
  private String iinBlackList;

  @Override
  public ScoringResponse score(ScoringRequest request) {
    clientsService.checkRequestIinSameClientIin(request.getIin());

    PersonalInfoUtils.fillEmptyFormData(request.getPersonalInfo());

    loanService.addLoanRequestHistory(request);

    var order = ordersServices.createNewPrimaryLoanOrder(request);
    MDC.put("loanOrderId", order.getOrderId().toString());

    request.setLoanProduct(order.getLoanProduct());
    context.setVariablesHolder(
        new VariablesHolder(
            scoringVarsService.getScoringVarsMap(),
            objectMapper
        )
    );
    context.setRequestData(request);
    List<OrderDto> orderDtos = ordersServices
        .findAllOpenAlternativeLoansByIin(context.getRequestData().getIin());
    if (!CollectionUtils.isEmpty(orderDtos)) {
      return processRejectedLoan(order, "Need to repay issued alternative loans");
    }

    checkIfIinMocked(request);
    var result = requirement.check(context);
    order = ordersServices.updateScoringInfo(
        order.getOrderId(),
        context.getScoringInfo()
    );

    var loanEffectiveRate = Optional.ofNullable(context.getLoanSchedule())
        .map(LoanSchedule::getEffectiveRate)
        .map(BigDecimal::valueOf)
        .orElse(null);
    var loanInterestRate = Optional.ofNullable(context.getInterestRate())
        .orElse(null);

    if (ScoringResult.APPROVED.equals(result.getResult())) {
      return processApprovedLoan(order, result, loanEffectiveRate, loanInterestRate);

    } else if (ScoringResult.ALTERNATIVE.equals(result.getResult())) {
      log.info("Scoring Result for orderId={} iin={} is ALTERNATIVE. Going to calc alternatives",
          order.getOrderId(), order.getIin());
      return processAlternativeLoan(request, order, result, loanEffectiveRate);
    }
    var rejectReason = result.getErrorsString(",");
    return processRejectedLoan(order, rejectReason);
  }

  private ScoringResponse processRejectedLoan(OrderDto order, String rejectReason) {
    ScoringResponse resp = new ScoringResponse();
    try {
      order = ordersServices.rejectLoanOrder(order.getOrderId(), rejectReason);

      resp = ScoringResponse.builder()
          .result(ScoringResult.REFUSED)
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(rejectReason)
          .effectiveRate(order.getLoanEffectiveRate())
          .build();
      log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
    } catch (Exception e) {
      log.error("Exception during processing REJECTED loan {}", e);
    }
    return resp;
  }

  private ScoringResponse processAlternativeLoan(ScoringRequest request,
      OrderDto order,
      ScoringRequirementResult result,
      BigDecimal loanEffectiveRate) {
    ScoringResponse resp = new ScoringResponse();
    try {
      var rejectReason = result.getErrorsString(",");
      order.setLoanEffectiveRate(loanEffectiveRate);
      var alternatives = alternativeLoanCalculation
          .calculateAlternative(context);

      if (Objects.nonNull(alternatives) && !alternatives.isEmpty()) {
        log.info("{} alternatives calculated", alternatives.size());
        order = ordersServices.rejectLoanOrder(
            order.getOrderId(),
            OrderState.ALTERNATIVE,
            rejectReason
        );
        alternatives = ordersServices.createNewAlternativeLoanOrders(order, alternatives);
        resp = ScoringResponse.builder()
            .result(result.getResult())
            .orderId(order.getOrderId())
            .orderTime(order.getInsertedTime())
            .rejectText(rejectReason)
            .alternativeChoices(alternatives)
            .effectiveRate(order.getLoanEffectiveRate())
            .build();

        log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
      } else {
        return processRejectedLoan(order, rejectReason);
      }
    } catch (Exception e) {
      log.error("Exception during processing ALTERNATIVE loan {}", e);
    }
    return resp;
  }

  private ScoringResponse processApprovedLoan(OrderDto order,
      ScoringRequirementResult result,
      BigDecimal loanEffectiveRate,
      BigDecimal loanInterestRate) {
    ScoringResponse resp = new ScoringResponse();
    try {
      var orderResponse = mfoCoreService.getNewOrder(order);
      order = ordersServices.updateScoringVariables(OrderScoringVariables.builder()
              .orderId(order.getOrderId())
              .status(OrderState.APPROVED)
              .loanInterestRate(loanInterestRate)
              .loanEffectiveRate(loanEffectiveRate)
              .orderExtRefId(orderResponse.getNumber())
              .orderExtRefTime(orderResponse.getDateTime())
          .build());

      var contract = mfoCoreService.getNewContract(order);
      var key = order.getIin() + "/contract-" + order.getOrderId() + "-"
          + order.getOrderExtRefId() + "-" + order.getOrderExtRefTime() + ".pdf";
      storageService.put(
          key,
          new ByteArrayInputStream(Base64.decodeBase64(contract.getContract())),
          MediaType.APPLICATION_PDF_VALUE
      );
      order = ordersServices.updateLoanOrderContractRefs(
          order.getOrderId(),
          key,
          contract.getNumber(),
          contract.getDateTime()
      );
      resp = ScoringResponse.builder()
          .result(result.getResult())
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(result.getErrorsString(","))
          .effectiveRate(order.getLoanEffectiveRate())
          .build();
      log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
    } catch (Exception e) {
      log.error("Exception during processing APPROVED loan {}", e.getMessage());
    }
    return resp;
  }

  private void checkIfIinMocked(ScoringRequest request) {
    request.setWhiteList(Stream.of(iinWhiteList.split(","))
        .anyMatch(iin -> iin.equalsIgnoreCase(request.getIin())));

    request.setBlackList(Stream.of(iinBlackList.split(","))
        .anyMatch(iin -> iin.equalsIgnoreCase(request.getIin())));
  }
}
