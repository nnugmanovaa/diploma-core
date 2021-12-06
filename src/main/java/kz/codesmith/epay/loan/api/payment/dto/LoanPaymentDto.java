package kz.codesmith.epay.loan.api.payment.dto;

import com.fasterxml.jackson.annotation.JsonView;
import java.math.BigDecimal;
import java.util.Map;
import kz.codesmith.epay.core.shared.views.Views;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.payment.LoanRepayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonView(Views.Public.class)
public class LoanPaymentDto {
  private Integer paymentId;

  private String contractNumber;

  private String contractDate;

  private LoanRepayType loanRepayType;

  private BigDecimal amount;

  private String clientRef;

  private String currency;

  private String extRefId;

  private String extRefTime;

  private String extUuid;

  private String description;

  private Integer clientsId;

  private String mfoOrderTime;

  @Default
  private Integer remainAmount = 0;

  @JsonView(Views.Internal.class)
  private Map<String, Object> initPaymentResponse;

  @JsonView(Views.Internal.class)
  private AcquiringBaseStatus initPaymentStatus;

  private MfoProcessingStatus mfoProcessingStatus;

  private String mfoProcessingMessage;

  @JsonView(Views.Internal.class)
  private AcquiringOrderState orderState;

  @JsonView(Views.Internal.class)
  private String insertedTime;

  @JsonView(Views.Internal.class)
  private String updatedTime;

  private String card = "548318-######-0941";
}
