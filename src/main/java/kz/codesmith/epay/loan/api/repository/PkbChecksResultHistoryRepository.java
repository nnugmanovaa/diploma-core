package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import kz.codesmith.epay.loan.api.domain.PkbCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PkbChecksResultHistoryRepository extends JpaRepository<PkbCheckEntity, Integer> {

  List<PkbCheckEntity> getAllByIinAndRequestDateAfterAndStatus(
      String iin, LocalDateTime dateTime, String status);

}
