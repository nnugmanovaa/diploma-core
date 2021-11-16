package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;

public interface IScoreVariablesService {
  <T> T getValue(ScoringVars scoringVar, Class<T> targetClass);
}
