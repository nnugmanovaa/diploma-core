package kz.codesmith.epay.loan.api.model.scoring;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartScoringRequest {
  private String fullReport;
  private String standardReport;
  private String incomeReport;
  private Integer loanPeriod;
  private Double loanAmount;
  private String iin;
}
