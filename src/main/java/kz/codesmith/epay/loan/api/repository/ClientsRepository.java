package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientIdentificationStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientInfoDto;
import kz.codesmith.epay.loan.api.domain.clients.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientsRepository extends JpaRepository<ClientEntity, Integer> {

  Optional<ClientEntity> findFirstByClientName(String clientName);

  Optional<ClientEntity> findByClientName(String name);

  Optional<ClientEntity> findByIin(String iin);

  Page<ClientEntity> findAllByClientNameStartingWithOrderByClientName(String startTemplate,
      Pageable pageable);

  Optional<ClientEntity> findByClientNameAndAndClientsId(String name, Integer clientsId);

  @Query("select count(C) as cnt, C.status"
      + " from ClientEntity C where C.insertedBy in (:insertedBy) group by C.status ")
  List<Object[]> getClientsStatusesAndCount(
      @Param("insertedBy") List<String> insertedBy
  );


  @Query("select count(C) as cnt, C.status"
      + " from ClientEntity C group by C.status ")
  List<Object[]> getAllClientsStatusesAndCount();

  Integer countByStatus(SimpleStatus status);

  @Query("select C.clientsId as clientId from ClientEntity C "
      + "where C.insertedBy in (:insertedBy)")
  List<Integer> getClientsIdByInsertedBy(
      @Param("insertedBy") List<String> insertedBy
  );

  @Query("select C.clientsId as clientId from ClientEntity C")
  List<Integer> getClientsId();

  Page<ClientEntity> findAllByStatus(SimpleStatus status, Pageable pageable);

  @Modifying
  @Query("UPDATE ClientEntity c SET c.identificationStatus = :state "
      + "WHERE c.clientsId = :clientsId")
  void updateNewIdentificationStatus(
      @Param("clientsId") Integer clientsId,
      @Param("state") ClientIdentificationStatus state);

  @Modifying
  @Query("UPDATE ClientEntity c SET c.notificationToken = :token "
      + "WHERE c.clientsId = :clientsId")
  void updateNewNotificationToken(
      @Param("clientsId") Integer clientsId,
      @Param("token") String state);

  @Query("SELECT COUNT(C) FROM ClientEntity C WHERE "
      + " C.insertedTime between :startTime and :endTime "
      + " and ((:insertedBy) IS NULL OR C.insertedBy IN (:insertedBy))")
  Integer getNewWalletsCount(
      @Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime,
      @Param("insertedBy") List<String> insertedBy
  );


  @Query("SELECT COUNT(C) as cnt, C.identificationStatus  FROM ClientEntity C WHERE "
      + " (((:insertedBy) IS NULL OR C.insertedBy IN (:insertedBy))) "
      + "group by C.identificationStatus")
  List<Object[]> getWalletsCountByStatus(
      @Param("insertedBy") List<String> insertedBy
  );

  @Query("SELECT COUNT(C) as cnt, C.identificationStatus  FROM ClientEntity C "
      + "group by C.identificationStatus")
  List<Object[]> getAllWalletsCountByStatus();

  Optional<ClientEntity> findByIinAndClientName(String iin, String clientName);
}
