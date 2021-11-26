package kz.codesmith.epay.loan.api.domain.orders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.codesmith.epay.core.shared.converter.Utils;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderType;
import kz.codesmith.jsonb.GenericJsonDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "loan_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeDef(name = "JsonMapDataType", typeClass = GenericJsonDataType.class)
public class OrderEntity {

  @Id
  @Column(name = "order_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "order_id_seq")
  @SequenceGenerator(
      name = "order_id_seq",
      sequenceName = "loan_orders_id_seq", allocationSize = 1)
  private Integer orderId;

  @Column(name = "parent_order_id")
  private Integer parentOrderId;

  @Column(name = "client_id")
  @NotNull
  private Integer clientId;

  @Column(name = "client_info")
  @NotBlank
  private String clientInfo;

  @Enumerated(EnumType.STRING)
  @Column(name = "order_type")
  @NotNull
  private OrderType orderType;

  @Column(name = "iin")
  @NotNull
  private String iin;

  @Column(name = "msisdn")
  @NotNull
  private String msisdn;

  @Column(name = "personal_info")
  @Type(type = "JsonMapDataType")
  @NotNull
  private Map<String, Object> personalInfo = new HashMap<>();

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status")
  @NotNull
  private OrderState status;

  @Column(name = "loan_amount")
  @NotNull
  @Positive
  private BigDecimal loanAmount;

  @Column(name = "loan_period_months")
  @NotNull
  @Positive
  private Integer loanPeriodMonths;

  @Column(name = "loan_method")
  @NotNull
  private String loanMethod;

  @Column(name = "loan_product")
  @NotNull
  private String loanProduct;

  @Column(name = "loan_effective_rate")
  private BigDecimal loanEffectiveRate;

  @Column(name = "loan_interest_rate")
  private BigDecimal loanInterestRate;

  @Column(name = "closed_time")
  private LocalDateTime closedTime;

  @Column(name = "cashout_info")
  @Type(type = "JsonMapDataType")
  private Map<String, Object> cashOutInfo = new HashMap<>();

  @Column(name = "order_ext_ref_id")
  private String orderExtRefId;

  @Column(name = "order_ext_ref_time")
  private LocalDateTime orderExtRefTime;

  @Column(name = "contract_ext_ref_id")
  private String contractExtRefId;

  @Column(name = "contract_ext_ref_time")
  private LocalDateTime contractExtRefTime;

  @Column(name = "contract_document_s3_key")
  private String contractDocumentS3Key;

  @Column(name = "scoring_reject_reason")
  private String scoringRejectReason;

  @Column(name = "inserted_time", updatable = false)
  private LocalDateTime insertedTime;

  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "pre_score_request_id")
  private String preScoreRequestId;

  @Column(name = "face_matching")
  private Double faceMatching;

  @Column(name = "scoring_info")
  @Type(type = "JsonMapDataType")
  @NotNull
  private Map<String, Object> scoringInfo = new HashMap<>();

  @Column(name = "incomes_info")
  @Type(type = "JsonMapDataType")
  @NotNull
  private Map<String, Object> incomesInfo = new HashMap<>();

  @PrePersist
  public void toCreate() {
    setInsertedTime(Utils.now());
    setUpdatedTime(getInsertedTime());
  }

  @PreUpdate
  public void toUpdate() {
    setUpdatedTime(Utils.now());
  }
}
