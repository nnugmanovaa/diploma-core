package kz.codesmith.epay.loan.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.pkb.KdnScoreRequestDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.requirement.ScoringRequirementResult;
import kz.codesmith.epay.loan.api.service.IAlternativeLoanCalculationService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
import kz.codesmith.epay.loan.api.service.IScoringService;
import kz.codesmith.logger.Logged;
import kz.codesmith.springboot.validators.iin.Iin;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('MFO_ADMIN')")
public class ScoringController {

  private final ScoringContext context;
  private final IPkbScoreService pkbScoreService;
  private final IAlternativeLoanCalculationService alternativeLoanCalculation;
  private final IMfoCoreService mfoCoreService;
  private final ILoanOrdersService ordersServices;
  private final ObjectMapper objectMapper;
  private final IScoreVariablesService scoreVarService;
  private final IScoringService scoringService;

  @SneakyThrows
  @ApiOperation(
      value = "Start client loan process",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/start-loan-process", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('CLIENT_USER')")
  public ResponseEntity<ScoringResponse> score(@Valid @RequestBody ScoringRequest request) {
    return ResponseEntity.ok(scoringService.score(request));
  }

  private ResponseEntity<ScoringResponse> returnMinAlternative(OrderDto order,
      ScoringRequirementResult result, Double costOfLiving, BigDecimal loanEffectiveRate)
      throws JsonProcessingException {

    var rejectReason = result.getErrorsString(",");
    order = ordersServices.rejectLoanOrder(
        order.getOrderId(),
        OrderState.ALTERNATIVE,
        rejectReason
    );

    var orderResponse = mfoCoreService.getNewOrder(order);
    order = ordersServices.updateLoanOrderExtRefs(
        order.getOrderId(),
        orderResponse.getNumber(),
        orderResponse.getDateTime()
    );

    order = ordersServices.updateScoringInfoAndEffectiveRateValues(
        order.getOrderId(),
        context.getScoringInfo(),
        loanEffectiveRate
    );

    List<AlternativeChoiceDto> alternatives = alternativeLoanCalculation.calculateAlternative(
        BigDecimal.valueOf(costOfLiving),
        scoreVarService.getValue(ScoringVars.MIN_LOAN_PERIOD, Integer.class),
        context.getInterestRate().floatValue(),
        context.getRequestData().getLoanProduct(),
        context.getRequestData().getLoanMethod(),
        scoreVarService.getValue(ScoringVars.MAX_GESV, Double.class),
        order.getOrderId()
    );

    if (!CollectionUtils.isEmpty(alternatives)) {
      alternatives = ordersServices.createNewAlternativeLoanOrders(order, alternatives);
      alternatives.forEach(alternativeChoiceDto -> {
        ordersServices
            .updateEffectiveRateAndInterestRateValues(alternativeChoiceDto.getOrderId(),
                alternativeChoiceDto.getLoanEffectiveRate(),
                alternativeChoiceDto
                    .getLoanInterestRate()
                    .floatValue());
      });
      var resp = ScoringResponse.builder()
          .result(result.getResult())
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(rejectReason)
          .alternativeChoices(alternatives)
          .effectiveRate(order.getLoanEffectiveRate())
          .build();

      log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
      return ResponseEntity.ok(resp);
    } else {
      return getRejectOrderResponse(order, rejectReason);
    }
  }

  private ResponseEntity<ScoringResponse> getRejectOrderResponse(OrderDto order,
      String rejectReason) throws JsonProcessingException {
    order = ordersServices.rejectLoanOrder(
        order.getOrderId(),
        OrderState.REJECTED,
        rejectReason
    );
    var resp = ScoringResponse.builder()
        .result(ScoringResult.REFUSED)
        .orderId(order.getOrderId())
        .orderTime(order.getInsertedTime())
        .rejectText(rejectReason)
        .effectiveRate(order.getLoanEffectiveRate())
        .build();
    log.info("Scoring Final Response: {}", objectMapper.writeValueAsString(resp));
    return ResponseEntity.ok(resp);
  }

  @PreAuthorize("hasAnyAuthority('CLIENT_USER')")
  @ApiOperation(
      value = "Get Score result by ID.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(value = "/result/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScoringResponse> getOrder(@PathVariable("id") @NotNull Integer orderId) {
    return Optional.ofNullable(ordersServices.getOrderByUserOwner(orderId))
        .map(o -> ScoringResponse.builder()
            .orderId(o.getOrderId())
            .orderTime(o.getInsertedTime())
            .rejectText(o.getScoringRejectReason())
            .result(getScoreResult(o))
            .alternativeChoices(ordersServices.getAlternativeChoices(o))
            .build())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  private ScoringResult getScoreResult(OrderDto o) {
    switch (o.getStatus()) {
      case APPROVED:
        return ScoringResult.APPROVED;
      case ALTERNATIVE:
        return ScoringResult.ALTERNATIVE;
      case REJECTED:
        return ScoringResult.REFUSED;
      default:
        return null;
    }
  }

  @ApiOperation(
      value = "Score behavior",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/behavior/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> scoreBehavior(@PathVariable("iin") @Iin String iin) {
    return ResponseEntity.ok(pkbScoreService.getBehaviorScore(iin, true));
  }

  @ApiOperation(
      value = "KDN score",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/kdn", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getKdnScore(@RequestBody @Valid KdnScoreRequestDto request) {
    return ResponseEntity.ok(pkbScoreService.getKdnScore(
        request.getIin(),
        request.getBirthDate(),
        true,
        request.getFirstName(),
        request.getLastName(),
        request.getMiddleName()
    ));
  }

  @ApiOperation(
      value = "Fico score",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/fico/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getFicoScore(@PathVariable("iin") @Iin String iin
  ) {
    return ResponseEntity.ok(pkbScoreService.getFicoScore(iin));
  }

  @ApiOperation(
      value = "Closed contacts score",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(path = "/closed/contracts/{iin}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getContractSum(@PathVariable("iin") @Iin String iin) {
    return ResponseEntity.ok(pkbScoreService.getContractSum(iin, true));
  }

  @ApiOperation(
      value = "Get score cards",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(path = "/behavior/cards", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getScoreCards() {
    return ResponseEntity.ok(pkbScoreService.getScoreCards());
  }

}
