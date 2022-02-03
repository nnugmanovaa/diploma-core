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
@Table(name = "job_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailsEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_details_id_seq")
  @SequenceGenerator(
      name = "job_details_id_seq", sequenceName = "job_details_job_details_id_seq",
      allocationSize = 1)
  @Column(name = "job_details_id")
  private Integer jobDetailsId;

  @Column(name = "education")
  private String education;

  @Column(name = "employment")
  private String employment;

  @Column(name = "type_of_work")
  private String typeOfWork;

  @Column(name = "work_position")
  private String workPosition;

  @Column(name = "employer")
  private String employer;

  @Column(name = "monthly_income")
  private BigDecimal monthlyIncome;

  @Column(name = "additional_monthly_income")
  private BigDecimal additionalMonthlyIncome;

  @Column(name = "marital_status")
  private String maritalStatus;

  @Column(name = "number_of_kids")
  private Integer numberOfKids;

  @Column(name = "clients_id")
  private Integer clientsId;
}
