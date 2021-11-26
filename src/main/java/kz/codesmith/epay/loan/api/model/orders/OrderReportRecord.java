package kz.codesmith.epay.loan.api.model.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import kz.codesmith.epay.loan.api.model.pkb.kdn.DeductionsDetailed;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderReportRecord {

  private Integer orderId;
  private LocalDate date;
  private LocalDateTime insertedTime;
  private LocalDateTime updatedTime;
  private String iin;
  private String msisdn;
  private String client;
  private BigDecimal loanAmount;
  private Integer loanPeriodMonths;
  private String loanProduct;
  private OrderState status;
  private PersonalInfoDto personalInfoDto;
  private ScoringInfo scoringInfo;
  private List<DeductionsDetailed> deductionsInfo;
  private String scoringRejectionReason;
}
