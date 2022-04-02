package kz.codesmith.epay.loan.api.diploma.service;

import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;

public interface IScoringService {
  ScoringResponse score(ScoringRequest request);
}
