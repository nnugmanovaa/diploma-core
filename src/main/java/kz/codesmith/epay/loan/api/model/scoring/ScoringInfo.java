package kz.codesmith.epay.loan.api.model.scoring;

import java.math.BigDecimal;
import java.util.Map;
import lombok.Data;

@Data
public class ScoringInfo {

  private int score;
  private double badRate;
  private double kdn;
  private BigDecimal newKdn;
  private BigDecimal debt;
  private BigDecimal income;
  private double maxPaymentSum;
  private double maxContractSum;
  private boolean hasOverdue;
  private Map<String, Object> incomesInfo;
}
