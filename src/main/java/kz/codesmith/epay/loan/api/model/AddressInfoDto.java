package kz.codesmith.epay.loan.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressInfoDto {
  private String region;
  private String city;
  private String postalCode;
  private String street;
  private String house;
  private String apartment;
  private String periodOfResidence;
  private boolean addressValid;
}