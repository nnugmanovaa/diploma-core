package kz.codesmith.epay.loan.api.controller;

import io.swagger.annotations.ApiOperation;
import kz.codesmith.epay.loan.api.model.CheckResult;
import kz.codesmith.epay.loan.api.service.ILoanChecksService;
import kz.codesmith.logger.Logged;
import kz.codesmith.springboot.validators.iin.Iin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/stop-factor-check")
@RequiredArgsConstructor
public class LoanChecksController {

  private final ILoanChecksService checksService;

  @PreAuthorize("hasAnyAuthority('CLIENT_USER', 'MFO_ADMIN')")
  @ApiOperation(
      value = "Check iin for stop factors",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(value = "/{iin}")
  public ResponseEntity<CheckResult> stopFactorCheck(@PathVariable("iin") @Iin String iin) {
    return ResponseEntity.ok(checksService.stopFactorCheck(iin));
  }

  @PreAuthorize("hasAnyAuthority('CLIENT_USER', 'MFO_ADMIN')")
  @ApiOperation(
      value = "Locally check iin for stop factors using db data",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @GetMapping(value = "/local/{iin}")
  public ResponseEntity<CheckResult> stopFactorLocalCheck(@PathVariable("iin") @Iin String iin) {
    return ResponseEntity.ok(checksService.stopFactorLocalCheck(iin));
  }
}
