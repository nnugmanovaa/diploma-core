package kz.codesmith.epay.loan.api.repository;

import kz.codesmith.epay.loan.api.domain.IncomeInfoCatalogEntity;
import kz.codesmith.epay.loan.api.model.IncomeInfoType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IncomeInfoCatalogRepository extends
    JpaRepository<IncomeInfoCatalogEntity, Integer> {

  Page<IncomeInfoCatalogEntity> findAllByType(IncomeInfoType type, Pageable pageable);
}
