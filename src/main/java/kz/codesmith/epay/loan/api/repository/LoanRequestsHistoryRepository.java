package kz.codesmith.epay.loan.api.repository;

import kz.codesmith.epay.loan.api.domain.LoanRequestsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRequestsHistoryRepository extends
    JpaRepository<LoanRequestsHistoryEntity, Integer> {
}
