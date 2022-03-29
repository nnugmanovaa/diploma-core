package kz.codesmith.epay.loan.api.service;

import java.util.List;
import kz.codesmith.epay.core.shared.model.users.UserRole;

public interface IUserRolesCachedService {


  List<UserRole> findByUserId(Long userId);
}
