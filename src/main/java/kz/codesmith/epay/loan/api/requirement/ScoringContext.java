package kz.codesmith.epay.loan.api.requirement;

import java.math.BigDecimal;
import kz.codesmith.epay.loan.api.annotation.SpringRequirementContext;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeLoanParams;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.service.VariablesHolder;
import kz.payintech.LoanSchedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SpringRequirementContext
@RequiredArgsConstructor
@ToString
public class ScoringContext implements RequirementContext {

  @Setter
  @Getter
  private ScoringRequest requestData;

  @Getter
  @Setter
  private VariablesHolder variablesHolder;

  @Getter
  private ScoringInfo scoringInfo = new ScoringInfo();

  @Getter
  @Setter
  private LoanSchedule loanSchedule;

  @Getter
  @Setter
  private AlternativeLoanParams alternativeLoanParams;

  @Getter
  @Setter
  private BigDecimal interestRate;
}
