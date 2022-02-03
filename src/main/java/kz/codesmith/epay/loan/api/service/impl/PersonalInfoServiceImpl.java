package kz.codesmith.epay.loan.api.service.impl;

import java.util.Optional;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.loan.api.component.AddressInfoMapper;
import kz.codesmith.epay.loan.api.component.JobDetailsMapper;
import kz.codesmith.epay.loan.api.component.PassportInfoMapper;
import kz.codesmith.epay.loan.api.domain.AddressInfoEntity;
import kz.codesmith.epay.loan.api.domain.JobDetailsEntity;
import kz.codesmith.epay.loan.api.domain.PassportInfoEntity;
import kz.codesmith.epay.loan.api.model.AddressInfoDto;
import kz.codesmith.epay.loan.api.model.JobDetailsDto;
import kz.codesmith.epay.loan.api.model.PassportInfoDto;
import kz.codesmith.epay.loan.api.model.PersonalFullDataDto;
import kz.codesmith.epay.loan.api.repository.AddressInfoRepository;
import kz.codesmith.epay.loan.api.repository.JobDetailsRepository;
import kz.codesmith.epay.loan.api.repository.PassportInfoRepository;
import kz.codesmith.epay.loan.api.service.IPersonalInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonalInfoServiceImpl implements IPersonalInfoService {
  private final AddressInfoMapper addressInfoMapper;
  private final AddressInfoRepository addressInfoRepository;
  private final JobDetailsRepository jobDetailsRepository;
  private final JobDetailsMapper jobDetailsMapper;
  private final PassportInfoMapper passportInfoMapper;
  private final PassportInfoRepository passportInfoRepository;

  @Override
  @Transactional
  public AddressInfoDto saveAddressInfo(AddressInfoDto addressInfo, Integer clientsId) {
    Optional<AddressInfoEntity> address = addressInfoRepository.findByClientsId(clientsId);
    if (address.isPresent()) {
      return addressInfoMapper.toDto(address.get());
    }
    var addressInfoEntity = addressInfoMapper.toEntity(addressInfo);
    addressInfoEntity.setClientsId(clientsId);
    addressInfoEntity = addressInfoRepository.save(addressInfoEntity);
    return addressInfoMapper.toDto(addressInfoEntity);
  }

  @Override
  @Transactional
  public JobDetailsDto saveJobDetails(JobDetailsDto jobDetailsDto, Integer clientsId) {
    Optional<JobDetailsEntity> detailsEntity = jobDetailsRepository.findByClientsId(clientsId);
    if (detailsEntity.isPresent()) {
      return jobDetailsMapper.toDto(detailsEntity.get());
    }
    var jobDetailsEntity = jobDetailsMapper.toEntity(jobDetailsDto);
    jobDetailsEntity.setClientsId(clientsId);
    jobDetailsEntity = jobDetailsRepository.save(jobDetailsEntity);
    return jobDetailsMapper.toDto(jobDetailsEntity);
  }

  @Override
  @Transactional
  public PassportInfoDto savePassportInfo(PassportInfoDto passportInfoDto, Integer clientsId) {
    Optional<PassportInfoEntity> passportInfo = passportInfoRepository.findByClientsId(clientsId);
    if (passportInfo.isPresent()) {
      return passportInfoMapper.toDto(passportInfo.get());
    }
    var passportInfoEntity = passportInfoMapper.toEntity(passportInfoDto);
    passportInfoEntity.setClientsId(clientsId);
    passportInfoEntity = passportInfoRepository.save(passportInfoEntity);
    return passportInfoMapper.toDto(passportInfoEntity);
  }

  @Override
  public PersonalFullDataDto getPersonalFullDataByClientsId(Integer clientsId) {
    var addressEntity = addressInfoRepository.findByClientsId(clientsId)
        .orElse(null);
    var jobEntity = jobDetailsRepository.findByClientsId(clientsId)
        .orElse(null);
    var passportEntity = passportInfoRepository.findByClientsId(clientsId)
        .orElse(null);
    return new PersonalFullDataDto(addressInfoMapper.toDto(addressEntity),
        jobDetailsMapper.toDto(jobEntity), passportInfoMapper.toDto(passportEntity));
  }

  @Override
  @Transactional
  public PersonalFullDataDto updatePersonalFullData(PersonalFullDataDto personalFullDataDto,
      Integer clientsId) {

    var addressEntity = addressInfoRepository.findByClientsId(clientsId);
    if (addressEntity.isEmpty()) {
      throw new GeneralApiServerException(ApiErrorType.E500_NOT_FOUND);
    }

    var address = addressInfoMapper
        .toEntity(personalFullDataDto.getAddressInfoDto());
    address.setClientsId(clientsId);
    address.setAddressInfoId(addressEntity.get().getAddressInfoId());

    address = addressInfoRepository.save(address);

    var jobEntity = jobDetailsRepository.findByClientsId(clientsId);
    if (jobEntity.isEmpty()) {
      throw new GeneralApiServerException(ApiErrorType.E500_NOT_FOUND);
    }
    var job = jobDetailsMapper
        .toEntity(personalFullDataDto.getJobDetailsDto());

    job.setClientsId(clientsId);
    job.setJobDetailsId(jobEntity.get().getJobDetailsId());

    job = jobDetailsRepository.save(job);

    var passportEntity = passportInfoRepository.findByClientsId(clientsId);
    if (passportEntity.isEmpty()) {
      throw new GeneralApiServerException(ApiErrorType.E500_NOT_FOUND);
    }
    var passport = passportInfoMapper
        .toEntity(personalFullDataDto.getPassportInfoDto());
    passport.setClientsId(clientsId);
    passport.setPassportInfoId(passportEntity.get().getPassportInfoId());

    passport = passportInfoRepository.save(passport);

    return new PersonalFullDataDto(addressInfoMapper.toDto(address),
        jobDetailsMapper.toDto(job), passportInfoMapper.toDto(passport));

  }
}
