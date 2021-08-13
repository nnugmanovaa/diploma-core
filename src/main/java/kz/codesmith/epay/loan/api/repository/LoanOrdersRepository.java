package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanOrdersRepository extends JpaRepository<OrderEntity, Integer> {

  Optional<OrderEntity> findByOrderIdAndClientId(Integer orderId, Integer clientId);

  @Query("select o from OrderEntity o "
      + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
      + "and o.insertedTime between :startTime and :endTime"
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetween(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer orderId,
      Pageable pageRequest
  );

  @Query("select o from OrderEntity o "
      + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
      + "and o.insertedTime between :startTime and :endTime "
      + "and o.clientId = :clientId"
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndClient(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer clientId,
      Integer orderId,
      Pageable pageRequest
  );

  List<OrderEntity> findAllByInsertedTimeIsBetweenAndOrderIdAndClientId(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer orderId,
      Integer clientId
  );

  List<OrderEntity> findAllByInsertedTimeIsBetweenAndOrderId(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer orderId
  );

  List<OrderEntity> findAllByParentOrderIdAndClientId(Integer parentId, Integer clientId);
}
