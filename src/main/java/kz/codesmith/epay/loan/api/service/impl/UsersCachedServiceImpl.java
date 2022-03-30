package kz.codesmith.epay.loan.api.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotActiveApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.core.shared.model.institutions.OwnerSupportDto;
import kz.codesmith.epay.core.shared.model.users.UserCreateDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.core.shared.model.users.UserRole;
import kz.codesmith.epay.core.shared.model.users.UserUpdateDto;
import kz.codesmith.epay.loan.api.service.IUsersCachedService;
import kz.codesmith.epay.loan.api.util.UsersMapper;
import kz.codesmith.epay.security.domain.UserEntity;
import kz.codesmith.epay.security.domain.UserRolesEntity;
import kz.codesmith.epay.security.domain.UserRolesIdentity;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.codesmith.epay.security.repository.OwnerSupportRepository;
import kz.codesmith.epay.security.repository.UserRepository;
import kz.codesmith.epay.security.repository.UserRolesRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsersCachedServiceImpl implements IUsersCachedService {

  //private static final int PASSWORD_LENGTH = 12;

  private final UserRepository usersRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserContextHolder userContext;
  private final UsersMapper usersMapper;
  private final ModelMapper simpleMapper;
  private final UserRolesRepository usersRolesRepository;
  private final OwnerSupportRepository ownerRepository;
  private final SecureRandom random = new SecureRandom();
  private final SpringCacheBasedUserCache springCacheBasedUserCache;
  private final CacheManager cacheManager;

  @Override
  //@Cacheable(value = "users", unless = "#result!=null && !(#result.isActive())")
  public UserDto getByUsername(String username) {
    return usersRepository.findByUsername(username)
        .map(usersMapper::toDto).orElse(null);
  }

  @Override
  public Page<UserDto> getUsers(
      String username,
      SimpleStatus status,
      OwnerType ownerType,
      Pageable pageRequest
  ) {
    return usersRepository.findAllByUsernameOrStatusOrOwnerTypeAndOwnerId(
        username,
        status,
        ownerType,
        pageRequest
    ).map(usersMapper::toDto);
  }

  @Override
  public Page<UserDto> getUsersExcludeClients(
      String username,
      SimpleStatus status,
      OwnerType ownerType,
      Pageable pageRequest
  ) {
    return usersRepository.findAllByUsernameOrStatusOrOwnerTypeAndOwnerIdExcludeClients(
        username,
        status,
        pageRequest
    ).map(usersMapper::toDto);
  }

  @Override
  public UserDto getUserById(Long userId) {
    Optional<UserEntity> optional = usersRepository.findById(userId);
    if (!optional.isPresent()) {
      return null;
    }
    return usersMapper.toDto(optional.get());
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void setUserStatus(Long usersId, SimpleStatus status) {
    var user = usersRepository.findById(usersId)
        .orElseThrow(() -> new NotFoundApiServerException(ApiErrorTypeParamValues.USER, usersId));
    user.setStatus(status);
    springCacheBasedUserCache.removeUserFromCache(user.getUsername());
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void setUserStatus(Integer ownerId, OwnerType ownerType, SimpleStatus status) {
    UserEntity user = usersRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
    if (Objects.nonNull(user)) {
      user.setStatus(status);
      springCacheBasedUserCache.removeUserFromCache(user.getUsername());
    }
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void setUserStatusByOwnersList(
      List<Integer> ownerIds,
      OwnerType ownerType,
      SimpleStatus status,
      LocalDateTime updatedTime
  ) {
    usersRepository.setUserStatusByOwnerList(
        status,
        userContext.getContext().getUsername(),
        updatedTime,
        ownerType,
        ownerIds
    );
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public UserDto updateUser(@Valid UserUpdateDto user) {

    Optional<UserEntity> optUserEntity = usersRepository.findById(user.getUserId());

    if (optUserEntity.isEmpty()) {
      throw new NotFoundApiServerException(ApiErrorTypeParamValues.USER, user.getUserId());
    }

    UserEntity userEntity = optUserEntity.get();

    if (isOwnerLocked(userEntity.getOwnerType(), userEntity.getOwnerId())) {
      throw new NotActiveApiServerException(ApiErrorTypeParamValues.USER_OWNER);
    }

    userEntity.setStatus(user.getStatus());

    if (StringUtils.isNotBlank(user.getPassword())) {
      if (user.isPasswordEncoded()) {
        userEntity.setPassword(user.getPassword());
      } else {
        userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
      }
    }

    userEntity.setUpdatedBy(userContext.getContext().getUsername());

    userEntity = usersRepository.save(userEntity);

    long userId = userEntity.getUserId();

    usersRolesRepository.findByUserRolesIdentityUserId(userId).forEach(usersRolesRepository::delete);

    createUserRoles(userId, user.getRoles());

    springCacheBasedUserCache.removeUserFromCache(user.getUsername());

    return usersMapper.toDto(userEntity);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void removeUser(@NotNull Long userId) {
    usersRepository.findById(userId).ifPresent(u -> {
      u.setStatus(SimpleStatus.DELETED);
      u.setUpdatedBy(userContext.getContext().getUsername());
      usersRepository.save(u);
      springCacheBasedUserCache.removeUserFromCache(u.getUsername());
    });
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public UserDto createUser(@Valid UserCreateDto user) {
    List<OwnerType> allowedTypes = Arrays.asList(
        OwnerType.MERCHANT,
        OwnerType.OPERATOR,
        OwnerType.AGENT,
        OwnerType.SALES_POINT,
        OwnerType.BANK
    );

    if (!allowedTypes.contains(user.getOwnerType())) {
      throw new GeneralApiServerException(ApiErrorType.E500_ILLEGAL_OWNER_TYPE);
    }

    if (user.getOwnerId() != null) {
      if (isOwnerLocked(user.getOwnerType(), user.getOwnerId())) {
        throw new NotActiveApiServerException(ApiErrorTypeParamValues.USER_OWNER);
      }
    }
    user.setStatus(SimpleStatus.ACTIVE);
    var u = createUserUnchecked(user);
    springCacheBasedUserCache.removeUserFromCache(u.getUsername());
    return u;
  }


  @Override
  public boolean isOwnerLocked(OwnerType type, Integer id) {
    Optional<OwnerSupportDto> ownerSupportDto = ownerRepository.findByTypeAndId(type, id);

    if (ownerSupportDto.isEmpty()) {
      throw new NotFoundApiServerException(ApiErrorTypeParamValues.USER_OWNER);
    }

    return ownerSupportDto
        .map(OwnerSupportDto::getStatus)
        .map(s -> !SimpleStatus.ACTIVE.equals(s))
        .orElse(Boolean.FALSE);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  public void createDefaultClientUser(
      String password,
      Integer clientsId,
      String clientName,
      boolean isValidated,
      @NotNull UserRole... roles
  ) {

    createDefaultUser(
        password,
        OwnerType.CLIENT,
        clientsId,
        clientName,
        null,
        isValidated ? SimpleStatus.ACTIVE : SimpleStatus.INACTIVE,
        roles
    );
    springCacheBasedUserCache.removeUserFromCache(clientName);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void createDefaultUser(String password,
      OwnerType ownerType, Integer ownerId,
      Integer operatorsId, @NotNull UserRole... roles) {

    var username = createUserName(ownerType, ownerId);
    createDefaultUser(password, ownerType, ownerId, username,
        operatorsId, SimpleStatus.ACTIVE, roles);
    springCacheBasedUserCache.removeUserFromCache(username);

  }

  public void createDefaultUser(String password,
      OwnerType ownerType, Integer ownerId, String userName,
      Integer operatorsId, SimpleStatus status, @NotNull UserRole... roles) {

    UserCreateDto userDto = new UserCreateDto();
    userDto.setOperatorsId(operatorsId);
    userDto.setOwnerType(ownerType);
    userDto.setOwnerId(ownerId);
    userDto.setStatus(status);
    userDto.setUsername(userName);
    userDto.setPassword(password);
    userDto.setRoles(Arrays.asList(roles));

    createUserUnchecked(userDto);
  }

  @Override
  @CacheEvict(value = "users", allEntries = true)
  @Transactional
  public void createDefaultUserWithUsername(String username, String password, String firstname,
      String lastname, String email,OwnerType ownerType, Integer ownerId, Integer operatorsId,
      @NotNull UserRole... roles) {
    UserCreateDto userDto = new UserCreateDto();
    userDto.setOperatorsId(operatorsId);
    userDto.setOwnerType(ownerType);
    userDto.setOwnerId(ownerId);
    userDto.setStatus(SimpleStatus.ACTIVE);
    userDto.setUsername(username);
    userDto.setPassword(password);
    userDto.setFirstname(firstname);
    userDto.setLastname(lastname);
    userDto.setEmail(email);
    userDto.setRoles(Arrays.asList(roles));

    createUserUnchecked(userDto);
    springCacheBasedUserCache.removeUserFromCache(username);
  }

  private String createUserName(OwnerType type, Integer id) {
    return type.name().toLowerCase().concat("_").concat(id.toString()).concat("_user");
  }


  private void createUserRoles(Long userId, List<UserRole> roles) {
    roles.forEach(a -> {
      UserRolesEntity userRolesEntity = new UserRolesEntity();
      UserRolesIdentity userRolesIdentity = new UserRolesIdentity();
      userRolesIdentity.setRole(a.name());
      userRolesIdentity.setUserId(userId);
      userRolesEntity.setUserRolesIdentity(userRolesIdentity);
      usersRolesRepository.save(userRolesEntity);
    });
  }

  private UserDto createUserUnchecked(@Valid UserCreateDto user) {

    if (usersRepository.findByUsername(user.getUsername()).isPresent()) {
      throw new GeneralApiServerException(ApiErrorType.E500_USER_ALREADY_EXISTS);
    }

    UserEntity userEntity = simpleMapper.map(user, UserEntity.class);
    var userContext = this.userContext.getContext();
    userEntity.setStatus(user.getStatus());
    userEntity.setPassword(passwordEncoder.encode(user.getPassword()));
    userEntity.setUpdatedBy(userContext.getUsername());
    userEntity.setInsertedBy("root");
    userEntity.setUpdatedBy("root");

    userEntity = usersRepository.save(userEntity);

    long userId = userEntity.getUserId();

    createUserRoles(userId, user.getRoles());

    return usersMapper.toDto(userEntity);

  }

  @Override
  public String generateRandomPassword() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      String passwordChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_$%!";
      int randomIndex = random.nextInt(passwordChars.length());
      sb.append(passwordChars.charAt(randomIndex));
    }

    return sb.toString();
  }

  @Override
  public void updateUsername(String oldName, String newName) {
    usersRepository.updateUsername(oldName, newName);
    springCacheBasedUserCache.removeUserFromCache(oldName);
  }
}
