package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.AddressInfoDto;
import kz.codesmith.epay.loan.api.model.JobDetailsDto;
import kz.codesmith.epay.loan.api.model.PassportInfoDto;
import kz.codesmith.epay.loan.api.model.PersonalFullDataDto;

public interface IPersonalInfoService {
  AddressInfoDto saveAddressInfo(AddressInfoDto addressInfo, Integer clientsId);

  JobDetailsDto saveJobDetails(JobDetailsDto jobDetailsDto, Integer clientsId);

  PassportInfoDto savePassportInfo(PassportInfoDto passportInfoDto, Integer clientsId);

  PersonalFullDataDto getPersonalFullDataByClientsId(Integer clientsId);

  PersonalFullDataDto updatePersonalFullData(PersonalFullDataDto personalFullDataDto,
      Integer clientsId);
}
