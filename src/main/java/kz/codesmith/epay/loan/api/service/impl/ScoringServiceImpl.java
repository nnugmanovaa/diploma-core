package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import kz.codesmith.epay.loan.api.configuration.ScoringProperties;
import kz.codesmith.epay.loan.api.domain.OrderScoringVariables;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.PkbReportsDto;
import kz.codesmith.epay.loan.api.model.PkbReportsRequest;
import kz.codesmith.epay.loan.api.model.exception.KdnReportFailedException;
import kz.codesmith.epay.loan.api.model.exception.PkbConnectorReportsFailedException;
import kz.codesmith.epay.loan.api.model.exception.ScoringUnreachableException;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.pkb.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.model.pkb.kdn.Data;
import kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeLoanParams;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeRejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.OwnScoringResponseDto;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoUtils;
import kz.codesmith.epay.loan.api.model.scoring.RejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResultDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.model.scoring.StartScoringRequest;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.requirement.ScoringRequirement;
import kz.codesmith.epay.loan.api.service.IAlternativeLoanCalculationService;
import kz.codesmith.epay.loan.api.service.IClientsService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.epay.loan.api.service.IScoringService;
import kz.codesmith.epay.loan.api.service.StorageService;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.codesmith.logger.Logged;
import kz.payintech.ListLoanMethod;
import kz.payintech.LoanSchedule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.common.util.CollectionUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Logged
@Service
@RequiredArgsConstructor
public class ScoringServiceImpl implements IScoringService {
  private final LoanService loanService;
  private final ILoanOrdersService ordersServices;
  private final ScoringContext context;
  private final ScoringVarsService scoringVarsService;
  private final ObjectMapper objectMapper;
  private final ScoringRequirement requirement;
  private final IMfoCoreService mfoCoreService;
  private final StorageService storageService;
  private final IAlternativeLoanCalculationService alternativeLoanCalculation;
  private final IPkbScoreService pkbScoreService;
  private final ScoringProperties scoringProperties;
  private final IClientsService clientsService;
  private final RestTemplate restTemplate;
  private final LoanOrdersRepository loanOrdersRepository;

  @Value("${scoring.iin.whitelist}")
  private String iinWhiteList;

  @Value("${scoring.iin.backlist}")
  private String iinBlackList;

  private static final List<OrderState> CASHED_OUT_STATES = Arrays
      .asList(OrderState.CASHED_OUT_CARD, OrderState.CASHED_OUT_WALLET);

  @Override
  public ScoringResultDto processOwnScoring(ScoringRequest request, OrderDto order) {

    /*var kdnRequest = fillKdnRequest(request);
    try {
      ApplicationReport kdnReport;
      if (scoringProperties.isEnabled()) {
        kdnReport = pkbScoreService.getKdnReport(kdnRequest);
      } else {
        kdnReport = new ApplicationReport();
        kdnReport.setKdnScore(0.3);
        kdnReport.setIncome(100000d);
        kdnReport.setDebt(10d);
      }
      var kdnScore = kdnReport.getKdnScore();

      saveClientIncomesInfo(kdnReport);

      log.info("PKB KDN is {}", kdnScore);

      var debt = new BigDecimal(kdnReport.getDebt());
      var income = new BigDecimal(kdnReport.getIncome());
      context.getScoringInfo().setKdn(kdnScore);
      context.getScoringInfo().setDebt(debt);
      context.getScoringInfo().setIncome(income);

      if (kdnScore >= scoringProperties.getMaxKdn()) {
        log.info("(kdnScore >= maxKdn) {} >= {}", kdnScore, scoringProperties.getMaxKdn());
        return fillFailedResponse(RejectionReason.KDN_TOO_BIG);
      }*/

    try {
      var kdnRequest = fillKdnRequest(request);
      PkbReportsDto pkbReportsDto = pkbScoreService
          .getAllPkbReports(PkbReportsRequest.builder()
              .iin(request.getIin())
              .kdnRequest(kdnRequest)
          .build());

      OwnScoringResponseDto scoringResponse;
      if (scoringProperties.isEnabled()) {
        var scoringRequest = fillScoringRequest(request, pkbReportsDto);
        scoringResponse = getScore(scoringRequest);
      } else {
        String mockedResponse = "{\"client_id\":\"1\",\"date_application\":\"Df\",\"decil\":\"7\","
            + "\"result\":\"OK\",\"score\":\"535.0\"}";
        scoringResponse = objectMapper
            .readValue(mockedResponse, OwnScoringResponseDto.class);
      }

      var kdnScore = scoringResponse.getKdn();

      log.info("PKB KDN is {}", kdnScore);

      var debt = new BigDecimal(scoringResponse.getDebt());
      var income = new BigDecimal(scoringResponse.getIncome());
      context.getScoringInfo().setKdn(kdnScore);
      context.getScoringInfo().setDebt(debt);
      context.getScoringInfo().setIncome(income);

      if (kdnScore >= scoringProperties.getMaxKdn()) {
        log.info("(kdnScore >= maxKdn) {} >= {}", kdnScore, scoringProperties.getMaxKdn());
        return fillFailedResponse(RejectionReason.KDN_TOO_BIG);
      }

      Integer decil = scoringResponse.getDecil();
      log.info("DECIL: {}, for: {}", decil, scoringResponse.getClientId());

      context.getScoringInfo()
          .setDecil(decil);
      context.getScoringInfo()
          .setOwnScore(scoringResponse.getScore());

      if (decil > scoringProperties.getMaxDecil()) {
        log.info("(decil >= maxDecil) {} >= {}", decil, scoringProperties.getMaxDecil());
        return fillFailedResponse(RejectionReason.DECIL_TOO_BIG);
      }
      log.info("Going to calculate new kdn for {}", request.getIin());


      float loanInterestRate;
      LoanSchedule loanSchedule;

      if (!request.getLoanMethod()
          .equals(ListLoanMethod.REPAYMENT_DEBT_INTEREST_END_PERIOD)) {
        loanInterestRate = scoringProperties.getInterestRate();
        loanSchedule = getLoanSchuduleCalculation(request, loanInterestRate);
        var maxGesv = context.getVariablesHolder().getValue(ScoringVars.MAX_GESV, Double.class);

        log.info(
            "Effective Rate Check [loanEffectiveRate={} maxGesv={}]",
            loanSchedule.getEffectiveRate(),
            maxGesv
        );

        if (loanSchedule.getEffectiveRate() > maxGesv) {
          return fillFailedResponse(RejectionReason.BAD_EFFECTIVE_RATE);
        }
      } else {
        loanInterestRate = request.getLoanPeriod() <= 30
            ? (scoringProperties.getPdlMaxInterestRate() / 30)
            : (scoringProperties.getPdlMaxInterestRate() / request.getLoanPeriod());
        loanSchedule = getLoanSchuduleCalculation(request, loanInterestRate);
      }
      context.setLoanSchedule(loanSchedule);
      context.setInterestRate(BigDecimal
          .valueOf(loanInterestRate));

      var period = new BigDecimal(request.getLoanPeriod());
      var avgMonthlyPayment = context
          .getLoanSchedule()
          .getTotalAmountToBePaid()
          .divide(period, 2, RoundingMode.HALF_UP);
      var newKdn = avgMonthlyPayment
          .add(debt)
          .divide(income, 2, RoundingMode.HALF_UP);
      context.getScoringInfo().setNewKdn(newKdn);
      log.info("New KDN is {}", newKdn);

      if (newKdn.doubleValue() > scoringProperties.getMaxKdn()) {
        log.info("(newKdn > maxKdn) {} > {}. return AlternativeRejectionReason.NEW_KDN_TOO_BIG",
            newKdn, scoringProperties.getMaxKdn());
        context.setAlternativeLoanParams(new AlternativeLoanParams(income, debt));
        return ScoringResultDto.builder()
            .scoringResult(ScoringResult.ALTERNATIVE)
            .message(AlternativeRejectionReason
                .NEW_KDN_TOO_BIG.description())
            .build();
      }
      return ScoringResultDto.builder()
          .scoringResult(ScoringResult.APPROVED)
          .message("OK")
          .build();
    } catch (KdnReportFailedException e) {
      return fillFailedResponse(RejectionReason.KDN_INCOME_OR_DEBT_UNAVAILABLE);
    } catch (PkbConnectorReportsFailedException e) {
      return fillFailedResponse(RejectionReason.LOAD_PKB_REPORTS_FAILED);
    } catch (Exception e) {
      return fillFailedResponse(RejectionReason.SCORING_ERRORS);
    }
  }

  private void saveClientIncomesInfo(ApplicationReport kdnReport) {
    Data deductionsData;
    if (kdnReport != null
        && kdnReport.getIncomesResultCrtrV2() != null
        && kdnReport.getIncomesResultCrtrV2().getResult() != null
        && kdnReport.getIncomesResultCrtrV2().getResult().getResponseData() != null) {

      deductionsData = kdnReport.getIncomesResultCrtrV2()
          .getResult()
          .getResponseData()
          .getData();
      context.getScoringInfo().setIncomesInfo(
          objectMapper.convertValue(deductionsData, Map.class));
    }
  }

  private KdnRequest fillKdnRequest(ScoringRequest request) {
    return KdnRequest.builder()
        .iin(request.getIin())
        .birthdate(request.getPersonalInfo().getBirthDate())
        .firstname(request.getPersonalInfo().getFirstName())
        .lastname(request.getPersonalInfo().getLastName())
        .middlename(request.getPersonalInfo().getMiddleName())
        .build();
  }

  private StartScoringRequest fillScoringRequest(ScoringRequest request,
      PkbReportsDto pkbReportsDto) {
    return StartScoringRequest.builder()
        .fullReport(pkbReportsDto.getFullReport())
        .standardReport(pkbReportsDto.getStandardReport())
        .incomeReport(pkbReportsDto.getIncomeReport())
        .loanAmount(request.getLoanAmount())
        .loanPeriod(request.getLoanPeriod())
        .iin(request.getIin())
        .build();
  }

  private LoanSchedule getLoanSchuduleCalculation(ScoringRequest request, float loanInterestRate) {
    return mfoCoreService.getLoanScheduleCalculation(
        BigDecimal.valueOf(request.getLoanAmount()),
        request.getLoanPeriod(),
        loanInterestRate,
        request.getLoanProduct(),
        request.getLoanMethod());
  }

  private ScoringResultDto fillFailedResponse(RejectionReason reason) {
    return ScoringResultDto.builder()
        .scoringResult(ScoringResult.REFUSED)
        .message(reason.description())
        .build();
  }

  @Override
  public ScoringResponse score(ScoringRequest request) {
    clientsService.checkRequestIinSameClientIin(request.getIin());

    PersonalInfoUtils.fillEmptyFormData(request.getPersonalInfo());

    loanService.addLoanRequestHistory(request);

    OrderDto order = createNewScoringLoanOrder(request);

    context.setVariablesHolder(
        new VariablesHolder(
            scoringVarsService.getScoringVarsMap(),
            objectMapper
        )
    );
    context.setRequestData(request);
    if (scoringProperties.isCheckOpenLoans()) {
      List<OrderEntity> orders = loanOrdersRepository
          .findAllByIinAndStatusIn(context.getRequestData().getIin(), CASHED_OUT_STATES);
      if (!CollectionUtils.isEmpty(orders)) {
        return processRejectedLoan(order, "Need to repay issued loans");
      }
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
      return processApprovedLoan(order, loanEffectiveRate,
          loanInterestRate, result.getErrorsString(","));

    } else if (ScoringResult.ALTERNATIVE.equals(result.getResult())) {
      log.info("Scoring Result for orderId={} iin={} is ALTERNATIVE. Going to calc alternatives",
          order.getOrderId(), order.getIin());
      return processAlternativeLoan(order, loanEffectiveRate);
    }
    var rejectReason = result.getErrorsString(",");
    return processRejectedLoan(order, rejectReason);
  }

  @Override
  public ScoringResponse startLoanProcess(ScoringRequest request) {
    clientsService.checkRequestIinSameClientIin(request.getIin());

    PersonalInfoUtils.fillEmptyFormData(request.getPersonalInfo());

    loanService.addLoanRequestHistory(request);

    OrderDto order = createNewScoringLoanOrder(request);

    context.setVariablesHolder(
        new VariablesHolder(
            scoringVarsService.getScoringVarsMap(),
            objectMapper
        )
    );
    context.setRequestData(request);
    if (scoringProperties.isCheckOpenLoans()) {
      List<OrderEntity> orders = loanOrdersRepository
          .findAllByIinAndStatusIn(context.getRequestData().getIin(), CASHED_OUT_STATES);
      if (!CollectionUtils.isEmpty(orders)) {
        return processRejectedLoan(order, "Need to repay issued loans");
      }
    }

    ScoringResultDto scoringResultDto = processOwnScoring(request, order);

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

    switch (scoringResultDto.getScoringResult()) {
      case APPROVED: {
        return processApprovedLoan(order, loanEffectiveRate,
            loanInterestRate, scoringResultDto.getMessage());
      }
      case ALTERNATIVE: {
        return processAlternativeLoan(order, loanEffectiveRate);
      }
      case REFUSED: {
        return processRejectedLoan(order, scoringResultDto.getMessage());
      }
      default: {
        throw new IllegalStateException();
      }
    }
  }

  private OrderDto createNewScoringLoanOrder(ScoringRequest request) {
    var order = ordersServices.createNewPrimaryLoanOrder(request);
    MDC.put("loanOrderId", order.getOrderId().toString());
    request.setLoanProduct(order.getLoanProduct());
    return order;
  }

  private ScoringResponse processRejectedLoan(OrderDto order, String rejectReason) {
    ScoringResponse resp = new ScoringResponse();
    try {
      order = ordersServices.rejectLoanOrder(order.getOrderId(), rejectReason);
      ScoringInfo scoringInfo = getScoringInfo();
      resp = ScoringResponse.builder()
          .result(ScoringResult.REFUSED)
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(rejectReason)
          .effectiveRate(order.getLoanEffectiveRate())
          .scoringInfo(scoringInfo)
          .build();
      log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
    } catch (Exception e) {
      log.error("Exception during processing REJECTED loan {}", e);
    }
    return resp;
  }

  private ScoringResponse processAlternativeLoan(OrderDto order,
      BigDecimal loanEffectiveRate) {
    ScoringResponse resp = new ScoringResponse();
    try {
      var rejectReason = AlternativeRejectionReason.NEW_KDN_TOO_BIG
          .description();
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
        ScoringInfo scoringInfo = getScoringInfo();
        resp = ScoringResponse.builder()
            .result(ScoringResult.ALTERNATIVE)
            .orderId(order.getOrderId())
            .orderTime(order.getInsertedTime())
            .rejectText(rejectReason)
            .alternativeChoices(alternatives)
            .effectiveRate(order.getLoanEffectiveRate())
            .scoringInfo(scoringInfo)
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
      BigDecimal loanEffectiveRate,
      BigDecimal loanInterestRate,
      String message) {
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
      ScoringInfo scoringInfo = getScoringInfo();
      resp = ScoringResponse.builder()
          .result(ScoringResult.APPROVED)
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(message)
          .effectiveRate(order.getLoanEffectiveRate())
          .scoringInfo(scoringInfo)
          .build();
      log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
    } catch (Exception e) {
      log.error("Exception during processing APPROVED loan {}", e.getMessage());
    }
    return resp;
  }

  private ScoringInfo getScoringInfo() {
    ScoringInfo scoringInfo = context.getScoringInfo();
    scoringInfo.setIncomesInfo(null);
    return scoringInfo;
  }

  private void checkIfIinMocked(ScoringRequest request) {
    request.setWhiteList(Stream.of(iinWhiteList.split(","))
        .anyMatch(iin -> iin.equalsIgnoreCase(request.getIin())));

    request.setBlackList(Stream.of(iinBlackList.split(","))
        .anyMatch(iin -> iin.equalsIgnoreCase(request.getIin())));
  }

  private OwnScoringResponseDto getScore(StartScoringRequest request) {
    var requestEntity = new HttpEntity<>(request);
    try {
      var response = restTemplate
          .postForEntity(scoringProperties.getOwnScoreUrl(),
              requestEntity,
              OwnScoringResponseDto.class);
      if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
        return response.getBody();
      }
    } catch (Exception e) {
      log.error("Unexpected exception: ", e);
      throw new ScoringUnreachableException(e.getMessage());
    }
    throw new ScoringUnreachableException(RejectionReason
        .SCORING_ERRORS.description());
  }
}
