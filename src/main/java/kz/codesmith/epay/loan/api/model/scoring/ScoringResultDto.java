package kz.codesmith.epay.loan.api.model.scoring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringResultDto {
  private ScoringResult scoringResult;
  private String message;
}
