package kz.codesmith.epay.loan.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kz.codesmith.epay.loan.api.domain.IncomeInfoCatalogEntity;
import kz.codesmith.epay.loan.api.model.IncomeInfoCatalogDto;
import kz.codesmith.epay.loan.api.model.IncomeInfoType;
import kz.codesmith.epay.loan.api.repository.IncomeInfoCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeInfoCatalogService {

  private final IncomeInfoCatalogRepository catalogRepository;
  private final ModelMapper mapper;

  public Page<IncomeInfoCatalogDto> getPagedAndSortedCatalogItems(IncomeInfoType type,
                                                                  Pageable pageable) {
    Page<IncomeInfoCatalogEntity> all = catalogRepository.findAllByType(type, pageable);
    List<IncomeInfoCatalogDto> ret = all.stream()
        .map(r -> mapper.map(r, IncomeInfoCatalogDto.class)).collect(Collectors.toList());
    return new PageImpl<>(ret, pageable, all.getTotalElements());
  }

  @Transactional
  public IncomeInfoCatalogDto upsertCatalogItem(IncomeInfoCatalogDto dto) {
    IncomeInfoCatalogEntity entity =
        catalogRepository.saveAndFlush(mapper.map(dto, IncomeInfoCatalogEntity.class));
    return mapper.map(entity, IncomeInfoCatalogDto.class);
  }

  @Transactional
  public void deleteCatalogItem(Integer id) {
    catalogRepository.deleteById(id);
  }

}
