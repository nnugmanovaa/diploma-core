package kz.codesmith.epay.loan.api.service.impl;

import javax.transaction.Transactional;
import kz.codesmith.epay.loan.api.domain.BlockedUserEntity;
import kz.codesmith.epay.loan.api.model.BlockedUserDto;
import kz.codesmith.epay.loan.api.model.map.BlockedUserMapper;
import kz.codesmith.epay.loan.api.repository.BlockedUserRepository;
import kz.codesmith.epay.loan.api.service.IBlockedUserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BlockedUserServiceImpl implements IBlockedUserService {

  private final BlockedUserMapper mapper;
  private final BlockedUserRepository blockedUserRepository;

  @Transactional
  @Override
  public BlockedUserDto createBlockedUser(BlockedUserDto blockedUserDto) {
    var blockedUserEntity = mapper.toEntity(blockedUserDto);
    blockedUserEntity = blockedUserRepository.save(blockedUserEntity);
    return mapper.toDto(blockedUserEntity);
  }

  @Override
  public Page<BlockedUserDto> getBlockedUsers(Pageable pageable) {
    return blockedUserRepository.findAll(pageable)
        .map(mapper::toDto);
  }

  @Override
  public void deleteBlockedUser(Long id) {
    blockedUserRepository.deleteById(id);
  }

  @Override
  public BlockedUserEntity findBlockedUserByIin(String iin) {
    return blockedUserRepository.findByIin(iin)
        .orElse(null);
  }
}
