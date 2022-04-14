package kz.codesmith.epay.loan.api.service;

import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.users.UserCreateDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.core.shared.model.users.UserRole;
import kz.codesmith.epay.core.shared.model.users.UserUpdateDto;
import kz.codesmith.epay.loan.api.diploma.model.SimpleClientPasswordChangeDto;
import kz.codesmith.epay.loan.api.diploma.model.UserPasswordChangeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUsersCachedService {

  UserDto getByUsername(String username);

  Page<UserDto> getUsers(
      String username,
      SimpleStatus status,
      OwnerType ownerType,
      Pageable pageRequest
  );

  Page<UserDto> getUsersExcludeClients(
      String username,
      SimpleStatus status,
      OwnerType ownerType,
      Pageable pageRequest
  );

  UserDto getUserById(Long userId);

  void setUserStatus(Long usersId, SimpleStatus status);

  void setUserStatus(Integer ownerId, OwnerType ownerType, SimpleStatus status);

  void setUserStatusByOwnersList(
      List<Integer> ownerIds,
      OwnerType ownerType,
      SimpleStatus status,
      LocalDateTime updatedTime
  );

  UserDto updateUser(@Valid UserUpdateDto user);

  void removeUser(@NotNull Long userId);

  UserDto createUser(@Valid UserCreateDto user);

  boolean isOwnerLocked(OwnerType type, Integer id);

  void createDefaultClientUser(
      String password,
      Integer clientsId,
      String clientName,
      boolean isValidated,
      @NotNull UserRole... roles
  );

  void createDefaultUser(String password,
      OwnerType ownerType, Integer ownerId,
      Integer operatorsId, @NotNull UserRole... roles);

  void createDefaultUserWithUsername(String username, String password,
      String firstname, String lastname, String email,
      OwnerType ownerType, Integer ownerId,
      Integer operatorsId, @NotNull UserRole... roles);

  String generateRandomPassword();

  void updateUsername(String oldName, String newName);

  UserDto resetUserPassword(UserPasswordChangeDto passwordChangeDto);

  UserDto resetClientPassword(SimpleClientPasswordChangeDto passwordChangeDto);
}
