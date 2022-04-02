package kz.codesmith.epay.loan.api.diploma.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
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
  private Integer numberOfKids;
  private BigDecimal monthlyIncome;
  private BigDecimal additionalMonthlyIncome;
  private Map<String, Object> incomesInfo = new HashMap<>();
  private Map<String, Object> additionalIncomesInfo = new HashMap<>();
}
