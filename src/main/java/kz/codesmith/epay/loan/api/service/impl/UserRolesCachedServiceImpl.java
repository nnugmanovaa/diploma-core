package kz.codesmith.epay.loan.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import kz.codesmith.epay.core.shared.model.users.UserRole;
import kz.codesmith.epay.loan.api.service.IUserRolesCachedService;
import kz.codesmith.epay.security.domain.UserRolesEntity;
import kz.codesmith.epay.security.domain.UserRolesIdentity;
import kz.codesmith.epay.security.repository.UserRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRolesCachedServiceImpl implements IUserRolesCachedService {

  private final UserRolesRepository usersRolesRepository;

  @Override
  public List<UserRole> findByUserId(Long userId) {
    return usersRolesRepository.findByUserRolesIdentityUserId(userId).stream()
        .map(UserRolesEntity::getUserRolesIdentity)
        .map(UserRolesIdentity::getRole)
        .map(UserRole::getInstance)
        .collect(Collectors.toList());
  }
}
