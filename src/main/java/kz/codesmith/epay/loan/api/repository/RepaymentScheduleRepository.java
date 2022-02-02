package kz.codesmith.epay.loan.api.repository;

import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.RepaymentScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentScheduleRepository
    extends JpaRepository<RepaymentScheduleEntity, Integer> {
  Optional<RepaymentScheduleEntity> findByOrderId(Integer orderId);
}
