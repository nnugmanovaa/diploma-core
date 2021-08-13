package kz.codesmith.epay.loan.api.repository;

import java.util.List;
import kz.codesmith.epay.loan.api.domain.PkbCheckManagementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PkbChecksManagementRepository extends
    JpaRepository<PkbCheckManagementEntity, String> {

  List<PkbCheckManagementEntity> getAllByIsActive(boolean isActive);

  void deleteByCode(String code);

}
