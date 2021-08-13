package kz.codesmith.epay.loan.api.repository;

import kz.codesmith.epay.loan.api.domain.ScoringVarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoringVarsRepository extends JpaRepository<ScoringVarEntity, Integer> {
}
