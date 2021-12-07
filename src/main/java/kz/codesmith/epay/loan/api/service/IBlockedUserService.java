package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.domain.BlockedUserEntity;
import kz.codesmith.epay.loan.api.model.BlockedUserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBlockedUserService {
  BlockedUserDto createBlockedUser(BlockedUserDto blockedUserDto);

  Page<BlockedUserDto> getBlockedUsers(Pageable pageable);

  void deleteBlockedUser(Long id);

  BlockedUserEntity findBlockedUserByIin(String iin);

}
