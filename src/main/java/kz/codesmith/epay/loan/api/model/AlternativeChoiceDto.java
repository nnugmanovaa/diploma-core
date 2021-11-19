package kz.codesmith.epay.loan.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.util.DecimalToIntSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlternativeChoiceDto {
  @JsonSerialize(using = DecimalToIntSerializer.class)
  private BigDecimal loanAmount;
  private BigDecimal loanInterestRate;
  private Integer loanMonthPeriod;
  private Integer orderId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime orderTime;
  private Float loanEffectiveRate;
}
