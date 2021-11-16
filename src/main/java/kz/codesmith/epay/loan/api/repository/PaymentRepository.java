package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {

  @Query(value =
      "select p.paymentId, p.mfoProcessingMessage "
          + "from PaymentEntity as p "
          + "where p.insertedTime between :startTime and :endTime "
          + "and p.mfoProcessingStatus in :processingStatus")
  List<Object[]> findPaymentIdByOrderTimeAndProcessingStatus(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("processingStatus") List<MfoProcessingStatus> processingStatus
  );
}
