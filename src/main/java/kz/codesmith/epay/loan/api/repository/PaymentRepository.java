package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Integer> {

  Page<PaymentEntity> findByLoanOrderId(Pageable pageable, Integer loanOrderId);

  Optional<PaymentEntity> findByLoanOrderId(Integer loanOrderId);

  Optional<PaymentEntity> findFirstByLoanOrderIdOrderByPaymentIdDesc(Integer loanOrderId);

  List<PaymentEntity> findAllByLoanOrderIdIn(List<Integer> loanOrdersId);
}
