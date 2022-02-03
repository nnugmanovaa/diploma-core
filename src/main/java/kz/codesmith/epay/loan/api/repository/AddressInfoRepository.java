package kz.codesmith.epay.loan.api.repository;

import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.AddressInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressInfoRepository extends JpaRepository<AddressInfoEntity, Integer> {
  Optional<AddressInfoEntity> findByClientsId(Integer clientsId);

}
