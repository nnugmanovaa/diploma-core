package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import kz.codesmith.epay.core.shared.model.clients.ClientCreateDto;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.loan.api.diploma.model.SimpleClientPasswordChangeDto;
import kz.codesmith.epay.loan.api.diploma.model.UserPasswordChangeDto;
import kz.codesmith.epay.loan.api.model.NewLoanRequest;
import kz.codesmith.epay.loan.api.model.NewLoanResp;
import kz.codesmith.epay.loan.api.service.IClientsServices;
import kz.codesmith.epay.loan.api.service.IUsersCachedService;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/clients")
@Logged
@RequiredArgsConstructor
public class ClientsMngmController {
  private final IClientsServices clientsServices;
  private final UserContextHolder userContextHolder;
  private final IUsersCachedService userService;

  @ApiOperation(
      value = "Creates Client.", notes = "In future only shortname & password will be required",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping("/create")
  public ResponseEntity<?> createNewClient(
      @RequestBody @Valid ClientCreateDto initDto
  ) {
    clientsServices.createNewClientWithAccountStructure(initDto, false);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(
      value = "Смена пароля у пользователя.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping("/change-password")
  public ResponseEntity<Void> resetPassword(
      @RequestBody @Valid UserPasswordChangeDto passwordChangeDto
  ) {
    userService.resetUserPassword(passwordChangeDto);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(
      value = "Get client info.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping("/{clientName}")
  public ResponseEntity<ClientDto> getClientInfo(
      @PathVariable("clientName") String clientName
  ) {
    return ResponseEntity.ok(clientsServices.getClientByClientName(clientName).get());
  }

  @PostMapping("/requestId")
  @ApiOperation("Register new request")
  public ResponseEntity<NewLoanResp> registerNewRequest(
      Authentication auth,
      @Valid @RequestBody NewLoanRequest newLoanRequest
  ) {
    String requestId = String.valueOf(RandomUtils.nextLong());
    return ResponseEntity.ok().body(new NewLoanResp(requestId));
  }
}
