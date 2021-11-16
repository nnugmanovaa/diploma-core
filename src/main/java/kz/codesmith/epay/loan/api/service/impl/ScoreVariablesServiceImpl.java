package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import kz.codesmith.epay.loan.api.requirement.ScoringContext;
import kz.codesmith.epay.loan.api.service.IScoreVariablesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoreVariablesServiceImpl implements IScoreVariablesService {
  private final ScoringContext scoringContext;

  @Override
  public <T> T getValue(ScoringVars scoringVar, Class<T> targetClass) {
    return scoringContext.getVariablesHolder().getValue(scoringVar, targetClass);
  }
}
