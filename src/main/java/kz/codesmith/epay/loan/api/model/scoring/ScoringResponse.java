package kz.codesmith.epay.loan.api.model.scoring;

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
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Data
@JsonInclude(Include.NON_NULL)
public class ScoringResponse {

  private ScoringResult result;
  private String rejectText;
  private List<AlternativeChoiceDto> alternativeChoices;
  private Integer orderId;
  private LocalDateTime orderTime;
  private BigDecimal effectiveRate;
}
