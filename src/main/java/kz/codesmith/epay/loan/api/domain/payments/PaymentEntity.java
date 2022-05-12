package kz.codesmith.epay.loan.api.domain.payments;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import kz.codesmith.epay.core.shared.converter.Utils;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.jsonb.GenericJsonDataType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

@Entity
@Table(name = "loan_payments")
@TypeDef(name = "JsonMapDataType", typeClass = GenericJsonDataType.class)
@Data
@NoArgsConstructor
public class PaymentEntity {

  @Id
  @Column(name = "payment_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "payment_id_seq")
  @SequenceGenerator(
      name = "payment_id_seq",
      sequenceName = "loan_payments_id_seq", allocationSize = 1)
  private Integer paymentId;

  @Column(name = "contract_number")
  private String contractNumber;

  @Column(name = "contract_date")
  private String contractDate;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "client_ref")
  private String clientRef;

  @Column(name = "clients_id")
  private Integer clientsId;

  @Column(name = "currency")
  private String currency;

  @Column(name = "ext_ref_id")
  private String extRefId;

  @Column(name = "ext_ref_time")
  private LocalDateTime extRefTime;

  @Column(name = "ext_uuid")
  private String extUuid;

  @Column(name = "description")
  private String description;

  @Column(name = "init_payment_response")
  @Type(type = "JsonMapDataType")
  private Map<String, Object> initPaymentResponse;

  @Column(name = "init_payment_status")
  @Enumerated(EnumType.STRING)
  private AcquiringBaseStatus initPaymentStatus;

  @Column(name = "mfo_processing_message")
  private String mfoProcessingMessage;

  @Column(name = "order_state")
  @Enumerated(EnumType.STRING)
  private AcquiringOrderState orderState;

  @Column(name = "inserted_time", updatable = false)
  private LocalDateTime insertedTime;

  @Column(name = "updated_time")
  private LocalDateTime updatedTime;

  @Column(name = "mfo_order_time")
  private LocalDateTime mfoOrderTime;

  @Column(name = "loan_order_id")
  private Integer loanOrderId;

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
