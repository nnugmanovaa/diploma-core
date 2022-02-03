package kz.codesmith.epay.loan.api.domain;

import java.time.LocalDate;
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
@Table(name = "passport_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassportInfoEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "passport_info_id_seq")
  @SequenceGenerator(
      name = "passport_info_id_seq", sequenceName = "passport_info_passport_info_id_seq",
      allocationSize = 1)
  @Column(name = "passport_info_id")
  private Integer passportInfoId;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "patronymic")
  private String patronymic;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "national_id_number")
  private String nationalIdNumber;

  @Column(name = "nationality")
  private String nationality;

  @Column(name = "national_id_issuer")
  private String nationalIdIssuer;

  @Column(name = "national_id_issue_date")
  private LocalDate nationalIdIssueDate;

  @Column(name = "national_id_valid_date")
  private LocalDate nationalIdValidDate;

  @Column(name = "is_ipdl")
  private Boolean isIpdl;

  @Column(name = "clients_id")
  private Integer clientsId;
}
