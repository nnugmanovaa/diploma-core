package kz.codesmith.epay.loan.api.repository;

import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.PassportInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassportInfoRepository extends JpaRepository<PassportInfoEntity, Integer> {
  Optional<PassportInfoEntity> findByClientsId(Integer clientsId);

}
