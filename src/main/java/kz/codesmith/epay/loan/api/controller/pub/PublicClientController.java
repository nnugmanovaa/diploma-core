package kz.codesmith.epay.loan.api.controller.pub;

import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.ClientExistDto;
import kz.codesmith.epay.loan.api.service.IClientsService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Logged
@RestController
@RequestMapping("/public/client")
@RequiredArgsConstructor
public class PublicClientController {
  private final IClientsService clientsService;

  @ApiOperation(value = "Check if client exist", produces = MediaType.APPLICATION_JSON_VALUE)
  @GetMapping("/{clientName}")
  public ResponseEntity<ClientExistDto> checkClientExist(
      @NotNull @PathVariable("clientName") @Valid String clientName
  ) {
    return ResponseEntity.ok(clientsService.checkClientExist(clientName));
  }
}
