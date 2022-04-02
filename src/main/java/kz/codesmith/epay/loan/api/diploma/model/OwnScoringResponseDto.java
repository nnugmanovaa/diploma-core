package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnScoringResponseDto {
  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("date_application")
  private String dateApplication;

  @JsonProperty("decil")
  private Integer decil;

  @JsonProperty("result")
  private String result;

  @JsonProperty("score")
  private Double score;

  @JsonProperty("kdn")
  private Double kdn;

  @JsonProperty("income")
  private String income;

  @JsonProperty("debt")
  private String debt;

  @JsonProperty("incomeInfo")
  private Map<String, Object> incomeInfo = new HashMap<>();
}
