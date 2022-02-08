package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.clients.ClientWithContactDto;
import kz.codesmith.epay.loan.api.model.ClientChangeNameDto;
import kz.codesmith.epay.loan.api.model.ClientEditDto;
import kz.codesmith.epay.loan.api.service.ICoreClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/clients")
@PreAuthorize("hasAnyAuthority('CLIENT_USER', 'AGENT_USER')")
public class ClientController {
  private final ICoreClientService coreClientService;

  @ApiOperation(
      value = "Client avatar load.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping("/upload")
  public ResponseEntity<String> uploadClientAvatar(
      @RequestBody MultipartFile file
  ) {
    return ResponseEntity.ok(coreClientService.uploadAvatar(file));
  }

  @ApiOperation(
      value = "Get client info.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping("/{clientName}")
  public ResponseEntity<ClientDto> getClientInfo(
      @PathVariable("clientName") String clientName
  ) {
    return ResponseEntity.ok(coreClientService.getClientByClientName(clientName));
  }

  @ApiOperation(
      value = "Edit clients profile",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PutMapping
  public ResponseEntity<ClientWithContactDto>
      updateClientProfile(@RequestBody ClientEditDto dto) {
    return ResponseEntity.ok(coreClientService.updateClientProfile(dto));
  }

  @ApiOperation(value = "Change client name", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("hasAnyAuthority('CLIENT_USER')")
  @PutMapping("/clientname")
  public ResponseEntity<ClientDto> changeClientName(
      @NotNull @Valid @RequestBody ClientChangeNameDto dto,
      HttpServletRequest request) {
    String requestTokenHeader = request.getHeader("Authorization");
    return ResponseEntity.ok(coreClientService.changeClientName(dto, requestTokenHeader));
  }
}
