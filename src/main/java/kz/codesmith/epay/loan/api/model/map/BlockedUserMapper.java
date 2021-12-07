package kz.codesmith.epay.loan.api.model.map;

import kz.codesmith.epay.loan.api.domain.BlockedUserEntity;
import kz.codesmith.epay.loan.api.model.BlockedUserDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    builder = @Builder(disableBuilder = true)
)
public interface BlockedUserMapper {
  BlockedUserDto toDto(BlockedUserEntity blockedUserEntity);

  BlockedUserEntity toEntity(BlockedUserDto blockedUserDto);
}
