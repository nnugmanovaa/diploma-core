package kz.codesmith.epay.loan.api.model.map;

import java.util.List;
import kz.codesmith.epay.loan.api.domain.RepaymentScheduleEntity;
import kz.codesmith.epay.loan.api.domain.ScheduleItemsEntity;
import kz.codesmith.epay.loan.api.model.schedule.RepaymentScheduleDto;
import kz.codesmith.epay.loan.api.model.schedule.ScheduleItemsDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.WARN,
    builder = @Builder(disableBuilder = true)
)
public interface RepaymentScheduleMapper {
  RepaymentScheduleDto toDto(RepaymentScheduleEntity entity);

  RepaymentScheduleEntity toEntity(RepaymentScheduleDto dto);

  ScheduleItemsDto itemsToDto(ScheduleItemsEntity entity);

  ScheduleItemsEntity itemsToEntity(ScheduleItemsDto dto);

  List<ScheduleItemsEntity> toItemsEntityList(List<ScheduleItemsDto> dtoList);

  List<ScheduleItemsDto> toItemsDtoList(List<ScheduleItemsEntity> dtoList);
}
