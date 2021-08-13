package kz.codesmith.epay.loan.api.service;

import java.util.List;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;

public interface IAlternativeLoanCalculation {

  List<AlternativeChoiceDto> calculateAlternative(ScoringContext context);
}
