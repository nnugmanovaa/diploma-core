package kz.codesmith.epay.loan.api.diploma.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import kz.codesmith.epay.loan.api.configuration.ScoringProperties;
import kz.codesmith.epay.loan.api.diploma.model.ScoringModel;
import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;
import kz.codesmith.epay.loan.api.diploma.service.IPkbConnectorService;
import kz.codesmith.epay.loan.api.diploma.service.IScoringService;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderType;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.repository.ScoringVarsRepository;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.payintech.ListLoanMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    if (scoringModel.getKdn() > scoringProperties.getMaxKdn()) {
      return ScoringResponse.builder()
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
    ordersRepository.save(orderEntity);
    return ScoringResponse.builder()
        .result(ScoringResult.APPROVED)
        .scoringInfo(scoringModel)
        .orderTime(LocalDateTime.now())
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
}
