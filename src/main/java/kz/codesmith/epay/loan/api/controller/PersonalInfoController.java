package kz.codesmith.epay.loan.api.controller;

import javax.validation.Valid;
import kz.codesmith.epay.loan.api.model.AddressInfoDto;
import kz.codesmith.epay.loan.api.model.JobDetailsDto;
import kz.codesmith.epay.loan.api.model.PassportInfoDto;
import kz.codesmith.epay.loan.api.model.PersonalFullDataDto;
import kz.codesmith.epay.loan.api.service.IPersonalInfoService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/clients")
@Logged
@PreAuthorize("hasAuthority('ADMIN')")
@RequiredArgsConstructor
public class PersonalInfoController {
  private final IPersonalInfoService personalInfoService;

  @PostMapping(path = "/address-info/{clientsId}")
  public ResponseEntity<AddressInfoDto> saveAddressInfo(@RequestBody @Valid AddressInfoDto
      addressInfoDto, @PathVariable Integer clientsId) {
    return ResponseEntity.ok(personalInfoService.saveAddressInfo(addressInfoDto, clientsId));
  }

  @PostMapping(path = "/job-details/{clientsId}")
  public ResponseEntity<JobDetailsDto> saveJobDetails(@RequestBody @Valid JobDetailsDto
      jobDetailsDto, @PathVariable Integer clientsId) {
    return ResponseEntity.ok(personalInfoService.saveJobDetails(jobDetailsDto, clientsId));
  }

  @PostMapping(path = "/passport-info/{clientsId}")
  public ResponseEntity<PassportInfoDto> savePassportInfo(@RequestBody @Valid PassportInfoDto
      passportInfoDto, @PathVariable Integer clientsId) {
    return ResponseEntity.ok(personalInfoService.savePassportInfo(passportInfoDto, clientsId));
  }

  @GetMapping(path = "/full-personal-info/{clientsId}")
  public ResponseEntity<PersonalFullDataDto> getPersonalFullDataByClientsId(
      @PathVariable Integer clientsId) {
    return ResponseEntity.ok(personalInfoService.getPersonalFullDataByClientsId(clientsId));
  }

  @PutMapping(path = "/update-personal-info/{clientsId}")
  public ResponseEntity<PersonalFullDataDto> updatePersonalFullData(
      @RequestBody @Valid PersonalFullDataDto personalFullDataDto,
      @PathVariable Integer clientsId) {
    return ResponseEntity.ok(personalInfoService.updatePersonalFullData(personalFullDataDto,
        clientsId));
  }
}
