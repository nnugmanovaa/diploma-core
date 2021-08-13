package kz.codesmith.epay.loan.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiOperation;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.pkb.KdnScoreRequestDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.requirement.ScoringRequirement;
import kz.codesmith.epay.loan.api.service.IAlternativeLoanCalculation;
import kz.codesmith.epay.loan.api.service.IClientsService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.epay.loan.api.service.StorageService;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.codesmith.epay.loan.api.service.impl.LoanService;
import kz.codesmith.epay.loan.api.service.impl.ScoringVarsService;
import kz.codesmith.logger.Logged;
import kz.codesmith.springboot.validators.iin.Iin;
import kz.payintech.LoanSchedule;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('MFO_ADMIN')")
public class ScoringController {

  private final LoanService loanService;
  private final ScoringRequirement requirement;
  private final ScoringContext context;
  private final IPkbScoreService pkbScoreService;
  private final IAlternativeLoanCalculation alternativeLoanCalculation;
  private final IMfoCoreService mfoCoreService;
  private final ILoanOrdersService ordersServices;
  private final StorageService storageService;
  private final ScoringVarsService scoringVarsService;
  private final ObjectMapper objectMapper;
  private final IClientsService clientsService;

  @Value("${scoring.iin.whitelist}")
  private String iinWhiteList;

  @ApiOperation(
      value = "Start client loan process",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/start-loan-process", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('CLIENT_USER')")
  public ResponseEntity<ScoringResponse> score(@Valid @RequestBody ScoringRequest request) {

    clientsService.checkRequestIinSameClientIin(request.getIin());

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
    request.setWhiteList(Stream.of(iinWhiteList.split(","))
        .anyMatch(iin -> iin.equalsIgnoreCase(request.getIin())));
    var result = requirement.check(context);

    order = ordersServices.updateScoringInfo(
        order.getOrderId(),
        context.getScoringInfo()
    );

    var loanEffectiveRate = Optional.ofNullable(context.getLoanSchedule())
        .map(LoanSchedule::getEffectiveRate)
        .map(BigDecimal::valueOf)
        .orElse(null);

    if (ScoringResult.APPROVED.equals(result.getResult())) {
      order = ordersServices.updateLoanOrderStatusAndLoanEffectiveRate(
          order.getOrderId(),
          OrderState.APPROVED,
          loanEffectiveRate
      );

      var orderResponse = mfoCoreService.getNewOrder(order);
      order = ordersServices.updateLoanOrderExtRefs(
          order.getOrderId(),
          orderResponse.getNumber(),
          orderResponse.getDateTime()
      );

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

      return ResponseEntity.ok(ScoringResponse.builder()
          .result(result.getResult())
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(result.getErrorsString(","))
          .effectiveRate(order.getLoanEffectiveRate())
          .build());

    } else if (ScoringResult.ALTERNATIVE.equals(result.getResult())) {
      var rejectReason = result.getErrorsString(",");
      order.setLoanEffectiveRate(loanEffectiveRate);
      var alternatives = alternativeLoanCalculation.calculateAlternative(context);
      if (Objects.nonNull(alternatives) && !alternatives.isEmpty()) {
        order = ordersServices.rejectLoanOrder(
            order.getOrderId(),
            OrderState.ALTERNATIVE,
            rejectReason
        );
        alternatives = ordersServices.createNewAlternativeLoanOrders(order, alternatives);
        return ResponseEntity.ok(ScoringResponse.builder()
            .result(result.getResult())
            .orderId(order.getOrderId())
            .orderTime(order.getInsertedTime())
            .rejectText(rejectReason)
            .alternativeChoices(alternatives)
            .effectiveRate(order.getLoanEffectiveRate())
            .build());
      } else {
        order = ordersServices.rejectLoanOrder(
            order.getOrderId(),
            OrderState.REJECTED,
            rejectReason
        );
        return ResponseEntity.ok(ScoringResponse.builder()
            .result(ScoringResult.REFUSED)
            .orderId(order.getOrderId())
            .orderTime(order.getInsertedTime())
            .rejectText(rejectReason)
            .effectiveRate(order.getLoanEffectiveRate())
            .build());
      }

    } else {
      var rejectReason = result.getErrorsString(",");
      order = ordersServices.rejectLoanOrder(order.getOrderId(), rejectReason);
      return ResponseEntity.ok(ScoringResponse.builder()
          .result(result.getResult())
          .orderId(order.getOrderId())
          .orderTime(order.getInsertedTime())
          .rejectText(rejectReason)
          .effectiveRate(order.getLoanEffectiveRate())
          .build());
    }
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
            .rejectText(o.getRejectReason())
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
