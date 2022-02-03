package kz.codesmith.epay.loan.api.component;

import javax.annotation.PostConstruct;
import kz.codesmith.epay.core.shared.converter.AbstractMapper;
import kz.codesmith.epay.loan.api.domain.PassportInfoEntity;
import kz.codesmith.epay.loan.api.model.PassportInfoDto;
import org.springframework.stereotype.Component;

@Component
public class PassportInfoMapper extends AbstractMapper<PassportInfoEntity, PassportInfoDto> {

  PassportInfoMapper() {
    super(PassportInfoEntity.class, PassportInfoDto.class);
  }

  @PostConstruct
  public void setupMapper() {
    mapper.createTypeMap(entityClass, dtoClass)
        .setPostConverter(toDtoConverter());
  }

}
