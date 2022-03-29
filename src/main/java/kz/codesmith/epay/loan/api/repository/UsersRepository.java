package kz.codesmith.epay.loan.api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.loan.api.domain.users.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String var1);

  UserEntity findByOwnerIdAndOwnerType(Integer var1, OwnerType var2);

  @Modifying
  @Query("update UserEntity S set S.status = ?1, S.updatedBy = ?2, S.updatedTime =?3 where S.ownerType = ?4 and S.ownerId in (?5)")
  void setUserStatusByOwnerList(SimpleStatus var1, String var2, LocalDateTime var3, OwnerType var4, List<Integer> var5);

  @Query("select U from UserEntity U where (:username is null or U.username=:username) and (:status is null or U.status=:status) and (:ownerType is null or U.ownerType=:ownerType)")
  Page<UserEntity> findAllByUsernameOrStatusOrOwnerTypeAndOwnerId(@Param("username") String var1, @Param("status") SimpleStatus var2, @Param("ownerType") OwnerType var3, Pageable var4);

  @Query("select U from UserEntity U where (:username is null or U.username=:username) and (:status is null or U.status=:status) and (NOT U.ownerType='CLIENT')")
  Page<UserEntity> findAllByUsernameOrStatusOrOwnerTypeAndOwnerIdExcludeClients(@Param("username") String var1, @Param("status") SimpleStatus var2, Pageable var3);

  @Query("select U from UserEntity U where U.ownerId in :ownerIds  and U.ownerType='AGENT'")
  List<UserEntity> getUsersByOwnerId(@Param("ownerIds") List<Integer> var1);

  @Modifying
  @Query("update UserEntity S set S.username =:newName where S.username =:oldName")
  void updateUsername(@Param("oldName") String var1, @Param("newName") String var2);
}
