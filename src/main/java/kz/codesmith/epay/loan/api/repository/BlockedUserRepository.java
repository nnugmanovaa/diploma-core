package kz.codesmith.epay.loan.api.repository;

import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.BlockedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUserEntity, Long> {
  Optional<BlockedUserEntity> findByIin(String iin);
}
