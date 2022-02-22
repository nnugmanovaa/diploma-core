package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;

public interface IScoringService {
  ScoringResponse score(ScoringRequest request);
}
