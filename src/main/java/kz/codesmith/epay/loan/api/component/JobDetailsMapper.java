package kz.codesmith.epay.loan.api.component;

import javax.annotation.PostConstruct;
import kz.codesmith.epay.core.shared.converter.AbstractMapper;
import kz.codesmith.epay.loan.api.domain.JobDetailsEntity;
import kz.codesmith.epay.loan.api.model.JobDetailsDto;
import org.springframework.stereotype.Component;

@Component
public class JobDetailsMapper extends AbstractMapper<JobDetailsEntity, JobDetailsDto> {
  JobDetailsMapper() {
    super(JobDetailsEntity.class, JobDetailsDto.class);
  }

  @PostConstruct
  public void setupMapper() {
    mapper.createTypeMap(entityClass, dtoClass)
        .setPostConverter(toDtoConverter());
  }
}
