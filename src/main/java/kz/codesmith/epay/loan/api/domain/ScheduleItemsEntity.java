package kz.codesmith.epay.loan.api.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repayment_schedule_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemsEntity {
  @Id
  @Column(name = "item_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "repayment_schedule_items_seq")
  @SequenceGenerator(
      name = "repayment_schedule_items_seq",
      sequenceName = "repayment_schedule_items_seq_id", allocationSize = 1)
  private Integer itemId;

  @Column(name = "repayment_schedule_id")
  @NotNull
  private Integer repaymentScheduleId;

  @Column(name = "payment_id")
  private Integer paymentId;

  @Column(name = "number_id")
  private Integer number;

  @Column(name = "payment_date")
  private String paymentDate;

  @Column(name = "amount_to_be_paid")
  private BigDecimal amountToBePaid;

  @Column(name = "reward")
  private BigDecimal reward;

  @Column(name = "total_amount_debt")
  private BigDecimal totalAmountDebt;

  @Column(name = "status")
  private String status;
}
