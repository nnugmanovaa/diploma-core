package kz.codesmith.epay.loan.api.domain;

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
@Table(name = "address_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressInfoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_info_id_seq")
  @SequenceGenerator(
      name = "address_info_id_seq", sequenceName = "address_info_address_info_id_seq",
      allocationSize = 1)
  @Column(name = "address_info_id")
  private Integer addressInfoId;

  @Column(name = "region")
  private String region;

  @Column(name = "city")
  private String city;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "street")
  private String street;

  @Column(name = "house")
  private String house;

  @Column(name = "apartment")
  private String apartment;

  @Column(name = "period_of_residence")
  private String periodOfResidence;

  @Column(name = "address_is_valid")
  private boolean addressValid;

  @Column(name = "clients_id")
  private Integer clientsId;
}