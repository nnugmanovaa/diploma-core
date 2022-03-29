package kz.codesmith.epay.loan.api.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kz.codesmith.epay.core.shared.model.institutions.OwnerSupportDto;
import kz.codesmith.epay.loan.api.domain.users.UserEntity;
import kz.codesmith.epay.loan.api.repository.UsersRepository;
import kz.codesmith.epay.security.model.EpayCoreUser;
import kz.codesmith.epay.security.model.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersDetailsServiceImpl implements UserDetailsService {
  private final UsersRepository usersRepository;


  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity user = (UserEntity)this.usersRepository.findByUsername(UserContext.fixUsername(username)).orElseThrow(() -> {
      return new UsernameNotFoundException("User not found");
    });
    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("CLIENT"));
    return new EpayCoreUser(user.getUsername(), user.getPassword(), true, true, true, true, authorities, user.getUserId(), user.getOwnerType(), user.getOwnerId(), user.getOperatorsId(), null);
  }
}
