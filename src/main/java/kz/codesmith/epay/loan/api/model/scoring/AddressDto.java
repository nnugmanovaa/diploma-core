package kz.codesmith.epay.loan.api.model.scoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddressDto {

  private String district;

  private String region;

  private String city;

  private String postalCode;

  private String street;

  private String house;

  private String apartment;

  private String country;

}
