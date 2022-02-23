package kz.codesmith.epay.loan.api.model.scoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
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
  private List<AlternativeChoiceDto> alternativeChoices;
  private Integer orderId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime orderTime;
  private BigDecimal effectiveRate;
  private ScoringInfo scoringInfo;
}
