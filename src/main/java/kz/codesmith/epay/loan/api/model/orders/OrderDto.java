package kz.codesmith.epay.loan.api.model.orders;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import kz.codesmith.epay.core.shared.model.AbstractDto;
import kz.codesmith.epay.loan.api.util.DecimalToIntSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class OrderDto extends AbstractDto {

  private Integer orderId;

  private Integer parentOrderId;

  private OrderDto parent;

  private Integer clientId;

  private String clientInfo;

  private OrderType orderType;

  private String iin;

  private String msisdn;

  private Map<String, Object> personalInfo;

  private OrderState status;

  @JsonSerialize(using = DecimalToIntSerializer.class)
  private BigDecimal loanAmount;

  private Integer loanPeriodMonths;

  private String loanMethod;

  private String loanProduct;

  private BigDecimal loanInterestRate;

  private BigDecimal loanEffectiveRate;

  private LocalDateTime closedTime;

  private Map<String, Object> cashOutInfo;

  private String orderExtRefId;

  private LocalDateTime orderExtRefTime;

  private String contractExtRefId;

  private LocalDateTime contractExtRefTime;

  @JsonProperty("rejectReason")
  private String scoringRejectReason;

  private String preScoreRequestId;

  private Map<String, Object> scoringInfo;

}
