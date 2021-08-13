package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.model.map.LoanMapper;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.repository.LoanRequestsHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

  private final LoanMapper mapper;
  private final LoanRequestsHistoryRepository requestsHistoryRepository;

  public void addLoanRequestHistory(ScoringRequest request) {
    requestsHistoryRepository.saveAndFlush(mapper.requestToHistoryEntity(request));
  }

}
