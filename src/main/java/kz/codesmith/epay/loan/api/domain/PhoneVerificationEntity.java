package kz.codesmith.epay.loan.api.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "phone_verification", schema = "loan")
public class PhoneVerificationEntity {

  @Id
  @Column(name = "verification_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "msisdn")
  private String msisdn;

  @Column(name = "code")
  private String code;

  @Column(name = "last_request_date")
  private LocalDateTime lastRequestDate;
}
