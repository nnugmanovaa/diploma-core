package kz.codesmith.epay.loan.api.domain;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "loan_requests_history", schema = "loan")
@Data
public class LoanRequestsHistoryEntity {

  @Id
  @Column(name = "loan_requests_history_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "seq_loan_requests_history")
  @SequenceGenerator(
      sequenceName = "loan_requests_history_seq_id",
      name = "seq_loan_requests_history",
      allocationSize = 1, schema = "loan")
  private Integer id;

  @Column(name = "iin")
  private String iin;

  @Column(name = "lastname")
  private String lastName;

  @Column(name = "firstname")
  private String firstName;

  @Column(name = "middlename")
  private String middleName;

  @Column(name = "birthdate")
  private LocalDate birthDate;

  @Column(name = "id_number")
  private String idNumber;

  @Column(name = "nationality")
  private String nationality;

  @Column(name = "issued_by")
  private String issuedBy;

  @Column(name = "issued_date")
  private LocalDate issuedDate;

  @Column(name = "expire_date")
  private LocalDate expireDate;

  @Column(name = "residence_oblast")
  private String residenceOblast;

  @Column(name = "residence_region")
  private String residenceRegion;

  @Column(name = "residence_city")
  private String residenceCity;

  @Column(name = "residence_postal_code")
  private String residencePostalCode;

  @Column(name = "residence_street")
  private String residenceStreet;

  @Column(name = "residence_house")
  private String residenceHouse;

  @Column(name = "residence_apartment")
  private String residenceApartment;

  @Column(name = "registration_oblast")
  private String registrationOblast;

  @Column(name = "registration_region")
  private String registrationRegion;

  @Column(name = "registration_city")
  private String registrationCity;

  @Column(name = "registration_postal_code")
  private String registrationPostalCode;

  @Column(name = "registration_street")
  private String registrationStreet;

  @Column(name = "registration_house")
  private String registrationHouse;

  @Column(name = "registration_apartment")
  private String registrationApartment;

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
  private Double monthlyIncome;

  @Column(name = "additional_monthly_income")
  private Double additionalMonthlyIncome;

  @Column(name = "work_experience")
  private Integer workExperience;

  @Column(name = "work_phone_num")
  private String workPhoneNum;

  @Column(name = "marital_status")
  private String maritalStatus;

  @Column(name = "number_of_kids")
  private Integer numberOfKids;

  @Column(name = "loan_amount")
  private Double loanAmount;

  @Column(name = "loan_period")
  private Integer loanPeriod;

  @Column(name = "loan_method")
  private String loanMethod;

  @Column(name = "pre_score_request_id")
  private String preScoreRequestId;

}
