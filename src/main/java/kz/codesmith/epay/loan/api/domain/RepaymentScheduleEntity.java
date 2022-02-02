package kz.codesmith.epay.loan.api.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repayment_schedule")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentScheduleEntity {
  @Id
  @Column(name = "repayment_schedule_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "repayment_schedule_seq")
  @SequenceGenerator(
      name = "repayment_schedule_seq",
      sequenceName = "repayment_schedule_seq_id", allocationSize = 1)
  private Integer repaymentScheduleId;

  @Column(name = "order_id")
  private Integer orderId;

  @Column(name = "amount_overpayment")
  private BigDecimal amountOverpayment;

  @Column(name = "amount_remain")
  private BigDecimal amountRemain;
}
