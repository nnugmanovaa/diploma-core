package kz.codesmith.epay.loan.api.diploma.controller;

import io.swagger.annotations.ApiOperation;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/score")
@RequiredArgsConstructor
public class ScoringController {

  @PreAuthorize("hasAnyAuthority('CLIENT_USER')")
  @ApiOperation(
      value = "Get Score result by ID.",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PostMapping(value = "/start-loan-process", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScoringResponse> score(@RequestBody ScoringRequest) {
  }
}
