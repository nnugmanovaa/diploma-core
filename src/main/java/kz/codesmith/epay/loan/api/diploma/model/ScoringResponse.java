package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@JsonInclude(Include.NON_NULL)
public class ScoringResponse {

  private ScoringResult result;
  private String rejectText;
  private Integer orderId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime orderTime;
  private BigDecimal effectiveRate;
  private ScoringModel scoringInfo;
}