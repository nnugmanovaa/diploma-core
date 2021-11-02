package kz.codesmith.epay.loan.api.repository;

import kz.codesmith.epay.loan.api.domain.LoanVarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LoanVarsRepository extends JpaRepository<LoanVarEntity, String> {

}
