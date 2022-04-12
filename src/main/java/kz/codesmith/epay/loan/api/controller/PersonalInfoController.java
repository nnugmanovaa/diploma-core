package kz.codesmith.epay.loan.api.controller;

import kz.codesmith.epay.loan.api.model.AddressInfoDto;
import kz.codesmith.epay.loan.api.model.JobDetailsDto;
import kz.codesmith.epay.loan.api.model.PassportInfoDto;
import kz.codesmith.epay.loan.api.model.PersonalFullDataDto;
import kz.codesmith.epay.loan.api.service.IPersonalInfoService;
import kz.codesmith.epay.security.model.UserContextHolder;
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
@PreAuthorize("hasAuthority('CLIENT_USER')")
@RequiredArgsConstructor
public class PersonalInfoController {
  private final IPersonalInfoService personalInfoService;
  private final UserContextHolder userContextHolder;

  @PostMapping(path = "/address-info")
  public ResponseEntity<AddressInfoDto> saveAddressInfo(@RequestBody AddressInfoDto addressInfoDto) {
    Integer clientsId = userContextHolder.getContext().getOwnerId();
    return ResponseEntity.ok(personalInfoService.saveAddressInfo(addressInfoDto, clientsId));
  }

  @PostMapping(path = "/job-details")
  public ResponseEntity<JobDetailsDto> saveJobDetails(@RequestBody JobDetailsDto jobDetailsDto) {
    Integer clientsId = userContextHolder.getContext().getOwnerId();
    return ResponseEntity.ok(personalInfoService.saveJobDetails(jobDetailsDto, clientsId));
  }

  @PostMapping(path = "/passport-info")
  public ResponseEntity<PassportInfoDto> savePassportInfo(@RequestBody PassportInfoDto passportInfoDto) {
    Integer clientsId = userContextHolder.getContext().getOwnerId();
    return ResponseEntity.ok(personalInfoService.savePassportInfo(passportInfoDto, clientsId));
  }

  @GetMapping(path = "/full-personal-info")
  public ResponseEntity<PersonalFullDataDto> getPersonalFullDataByClientsId() {
    Integer clientsId = userContextHolder.getContext().getOwnerId();
    return ResponseEntity.ok(personalInfoService.getPersonalFullDataByClientsId(clientsId));
  }

  @PutMapping(path = "/update-personal-info")
  public ResponseEntity<PersonalFullDataDto> updatePersonalFullData(
      @RequestBody PersonalFullDataDto personalFullDataDto) {
    Integer clientsId = userContextHolder.getContext().getOwnerId();
    return ResponseEntity.ok(personalInfoService.updatePersonalFullData(personalFullDataDto,
        clientsId));
  }
}
