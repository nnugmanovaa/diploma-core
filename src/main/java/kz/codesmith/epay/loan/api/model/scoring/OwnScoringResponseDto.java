package kz.codesmith.epay.loan.api.model.scoring;

import com.fasterxml.jackson.annotation.JsonProperty;
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
}
