package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResponse;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResultDto;

public interface IScoringService {
  ScoringResultDto processOwnScoring(ScoringRequest request, OrderDto order);

  ScoringResponse score(ScoringRequest request);

  ScoringResponse startLoanProcess(ScoringRequest request);
}
