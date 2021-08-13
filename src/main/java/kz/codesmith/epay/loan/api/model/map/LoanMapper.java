package kz.codesmith.epay.loan.api.model.map;

import kz.codesmith.epay.loan.api.domain.LoanRequestsHistoryEntity;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoanMapper {
  LoanRequestsHistoryEntity requestToHistoryEntity(ScoringRequest req);
}
