package kz.codesmith.epay.loan.api.diploma.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoringModel {
  private String iin;
  private BigDecimal loanAmount;
  private Integer numberOfActiveLoans;
  private Double income;
  private Double debt;
  private Integer maxOverdueDays;
  private Double maxOverdueAmount;
  private Double overduesSum;
  private Integer period;
  private Integer numberOfKids;
  private Integer clientAge;
}
