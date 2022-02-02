package kz.codesmith.epay.loan.api.repository;

import java.util.List;
import kz.codesmith.epay.loan.api.domain.ScheduleItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleItemsRepository
    extends JpaRepository<ScheduleItemsEntity, Integer> {
  List<ScheduleItemsEntity> findAllByRepaymentScheduleId(Integer id);
}
