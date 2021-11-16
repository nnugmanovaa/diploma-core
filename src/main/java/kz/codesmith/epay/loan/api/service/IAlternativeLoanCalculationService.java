package kz.codesmith.epay.loan.api.service;

import java.math.BigDecimal;
import java.util.List;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.payintech.ListLoanMethod;

public interface IAlternativeLoanCalculationService {

  List<AlternativeChoiceDto> calculateAlternative(ScoringContext context);

  List<AlternativeChoiceDto> calculateAlternative(BigDecimal loanAmount,
      Integer loanMonthPeriod,
      float clientInterestRate,
      String creditProduct,
      ListLoanMethod loanType,
      Double maxGesv,
      Integer orderId);
}
