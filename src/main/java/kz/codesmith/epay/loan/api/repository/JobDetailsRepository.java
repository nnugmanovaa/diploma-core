package kz.codesmith.epay.loan.api.repository;

import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.JobDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobDetailsRepository extends JpaRepository<JobDetailsEntity, Integer> {

  Optional<JobDetailsEntity> findByClientsId(Integer clientsId);


}
