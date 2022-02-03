package kz.codesmith.epay.loan.api.component;

import javax.annotation.PostConstruct;
import kz.codesmith.epay.core.shared.converter.AbstractMapper;
import kz.codesmith.epay.loan.api.domain.AddressInfoEntity;
import kz.codesmith.epay.loan.api.model.AddressInfoDto;
import org.springframework.stereotype.Component;

@Component
public class AddressInfoMapper extends AbstractMapper<AddressInfoEntity, AddressInfoDto> {
  AddressInfoMapper() {
    super(AddressInfoEntity.class, AddressInfoDto.class);
  }

  @PostConstruct
  public void setupMapper() {
    mapper.createTypeMap(entityClass, dtoClass)
        .setPostConverter(toDtoConverter());
  }
}
