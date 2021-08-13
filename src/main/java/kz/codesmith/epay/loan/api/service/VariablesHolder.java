package kz.codesmith.epay.loan.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVars;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
public class VariablesHolder {

  private final Map<String, String> scoringVarsMap;
  private final ObjectMapper objectMapper;

  public VariablesHolder(
      Map<String, String> scoringVarsMap,
      ObjectMapper objectMapper
  ) {
    this.scoringVarsMap = scoringVarsMap;
    this.objectMapper = objectMapper;
  }

  public String getValue(ScoringVars varEnum) {
    return getValue(varEnum, String.class);
  }

  @SneakyThrows
  public <T> T getValue(ScoringVars varEnum, Class<T> targetClass) {
    return objectMapper.readValue(scoringVarsMap.get(varEnum.name()), targetClass);
  }
}
