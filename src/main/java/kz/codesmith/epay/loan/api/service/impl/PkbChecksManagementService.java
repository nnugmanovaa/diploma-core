package kz.codesmith.epay.loan.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kz.codesmith.epay.loan.api.domain.PkbCheckManagementEntity;
import kz.codesmith.epay.loan.api.model.PkbCheckManagementDto;
import kz.codesmith.epay.loan.api.repository.PkbChecksManagementRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PkbChecksManagementService {

  private final PkbChecksManagementRepository managementRepository;
  private final ModelMapper mapper;

  public Page<PkbCheckManagementDto> getPagedAndSortedPkbChecks(Pageable pageable) {
    Page<PkbCheckManagementEntity> all = managementRepository.findAll(pageable);
    List<PkbCheckManagementDto> ret = all.stream()
        .map(r -> mapper.map(r, PkbCheckManagementDto.class)).collect(Collectors.toList());
    return new PageImpl<>(ret, pageable, all.getTotalElements());
  }

  @Transactional
  public PkbCheckManagementDto upsertPkbCheck(PkbCheckManagementDto dto) {
    PkbCheckManagementEntity entity =
        managementRepository.saveAndFlush(mapper.map(dto, PkbCheckManagementEntity.class));
    return mapper.map(entity, PkbCheckManagementDto.class);
  }

  @Transactional
  public void deletePkbCheckByCode(String code) {
    managementRepository.deleteByCode(code);
  }


}
