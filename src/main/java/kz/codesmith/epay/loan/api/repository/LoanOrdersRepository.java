package kz.codesmith.epay.loan.api.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanOrdersRepository extends JpaRepository<OrderEntity, Integer> {
  Optional<OrderEntity> findByOrderIdAndClientId(Integer orderId, Integer clientId);

  Optional<OrderEntity> findByContractExtRefId(String contractExtRefId);

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
      + "and o.status in (:states)"
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

  List<OrderEntity> findAllByIinAndStatusInAndOrderType(String iin,
      List<OrderState> states,
      OrderType orderType);

  @Query("select o from OrderEntity o "
      + " where o.status in (:orderStates) and "
      + " o.insertedTime between :startTime and :endTime ")
  List<OrderEntity> findAllByStatusAndTime(
      @Param("orderStates") List<OrderState> orderStates,
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  @Query("select o from OrderEntity o "
      + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
      + "and o.insertedTime between :startTime and :endTime "
      + "and o.status in (:states) "
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndStatus(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer orderId,
      List<OrderState> states,
      Pageable pageRequest
  );

  @Query("select o from OrderEntity o "
      + "where ((:orderId IS NULL) OR o.orderId = :orderId) "
      + "and o.insertedTime between :startTime and :endTime "
      + "and o.clientId = :clientId "
      + "and o.status in (:statuses) "
  )
  Page<OrderEntity> findAllByInsertedTimeIsBetweenAndClientAndStatus(
      LocalDateTime startTime,
      LocalDateTime endTime,
      Integer clientId,
      Integer orderId,
      List<OrderState> statuses,
      Pageable pageRequest
  );

  Optional<OrderEntity> findByOrderIdAndContractExtRefId(Integer orderId, String contractExtRefId);

  @Query(value = "select o.* from loan.loan_orders o where o.client_id = ?1 "
      + " and o.order_status = ?2 order by order_id desc limit 1", nativeQuery = true)
  Optional<OrderEntity> findByClientIdAndStatus(Integer clientId, String status);

  Optional<OrderEntity> findByOrderId(Integer orderId);

  Optional<OrderEntity> findByIinAndStatus(String iin, OrderState orderState);
}
