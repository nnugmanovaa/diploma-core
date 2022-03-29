package kz.codesmith.epay.loan.api.util;

import javax.annotation.PostConstruct;
import kz.codesmith.epay.core.shared.converter.AbstractMapper;
import kz.codesmith.epay.core.shared.model.institutions.OwnerSupportDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.loan.api.service.IUserRolesCachedService;
import kz.codesmith.epay.security.domain.UserEntity;
import kz.codesmith.epay.security.repository.OwnerSupportRepository;
import org.springframework.stereotype.Component;

@Component
public class UsersMapper extends AbstractMapper<UserEntity, UserDto> {

  private final IUserRolesCachedService userRolesCachedService;
  private final OwnerSupportRepository ownerSupportRepository;

  public UsersMapper(
      IUserRolesCachedService userRolesCachedService,
      OwnerSupportRepository ownerSupportRepository
  ) {
    super(UserEntity.class, UserDto.class);
    this.userRolesCachedService = userRolesCachedService;
    this.ownerSupportRepository = ownerSupportRepository;
  }

  @PostConstruct
  public void setupMapper() {
    mapper.createTypeMap(UserEntity.class, UserDto.class).setPostConverter(toDtoConverter());
    mapper.createTypeMap(UserDto.class, UserEntity.class).setPostConverter(toEntityConverter());
  }

  @Override
  public void mapSpecificFieldsToDto(UserEntity source, UserDto destination) {
    /*destination.setRoles(userRolesCachedService.findByUserId(source.getUserId()));
    destination.setOwnerName(ownerSupportRepository.findByTypeAndId(
        source.getOwnerType(),
        source.getOwnerId())
        .map(OwnerSupportDto::getName)
        .orElse(null)
    );*/
  }
}
