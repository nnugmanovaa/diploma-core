package kz.codesmith.epay.loan.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalFullDataDto {
  AddressInfoDto addressInfoDto;
  JobDetailsDto jobDetailsDto;
  PassportInfoDto passportInfoDto;
}
