package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import kz.codesmith.epay.core.shared.model.clients.ClientCreateDto;
import kz.codesmith.epay.loan.api.service.IClientsServices;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
}
