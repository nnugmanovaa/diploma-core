package kz.codesmith.epay.loan.api.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import kz.codesmith.epay.loan.api.domain.ScoringVarEntity;
import kz.codesmith.epay.loan.api.model.ScoringVarDto;
import kz.codesmith.epay.loan.api.repository.ScoringVarsRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScoringVarsService {

  private final ScoringVarsRepository scoringVarsRepository;
  private final ModelMapper mapper;

  public Page<ScoringVarDto> getAllScoringVars(Pageable pageable) {
    Page<ScoringVarEntity> all = scoringVarsRepository.findAll(pageable);
    List<ScoringVarDto> ret = all.stream()
        .map(r -> mapper.map(r, ScoringVarDto.class)).collect(Collectors.toList());
    return new PageImpl<>(ret, pageable, all.getTotalElements());
  }

  @Transactional
  public ScoringVarDto upsertScoringVar(ScoringVarDto dto) {
    ScoringVarEntity entity =
        scoringVarsRepository.saveAndFlush(mapper.map(dto, ScoringVarEntity.class));
    return mapper.map(entity, ScoringVarDto.class);
  }

  @Transactional
  public void deleteScoringVar(Integer id) {
    scoringVarsRepository.deleteById(id);
  }

  public Map<String, String> getScoringVarsMap() {
    Map<String, String> scoringVars = new HashMap<>();
    scoringVarsRepository.findAll()
        .forEach(r -> scoringVars.put(r.getConstantName(), r.getValue()));
    return scoringVars;
  }

}
