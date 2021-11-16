package kz.codesmith.epay.loan.api.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
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
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("orderId") Integer orderId,
      Pageable pageRequest
  );

  @Query("select o from OrderEntity o "
          + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
          + "and o.insertedTime between :startTime and :endTime "
          + "and ((:states IS NULL) OR o.status in :states)"
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndState(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("orderId") Integer orderId,
      @Param("states") List<OrderState> states,
      Pageable pageRequest
  );


  @Query("select o from OrderEntity o "
      + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
      + "and o.insertedTime between :startTime and :endTime "
      + "and o.clientId = :clientId"
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndClient(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("clientId") Integer clientId,
      @Param("orderId") Integer orderId,
      Pageable pageRequest
  );

  @Query("select o from OrderEntity o "
          + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
          + "and o.insertedTime between :startTime and :endTime "
          + "and o.clientId = :clientId "
          + "and o.status in (:states)"
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndClientAndState(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("orderId") Integer orderId,
      @Param("clientId") Integer clientId,
      @Param("states") List<OrderState> states,
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

  List<OrderEntity> findAllByIinAndStatusIn(String iin, List<OrderState> states);
}