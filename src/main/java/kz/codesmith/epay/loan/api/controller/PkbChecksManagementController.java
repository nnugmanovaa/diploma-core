package kz.codesmith.epay.loan.api.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.loan.api.model.PkbCheckManagementDto;
import kz.codesmith.epay.loan.api.service.impl.PkbChecksManagementService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Logged
@RestController
@RequestMapping("/checks-management")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MFO_ADMIN')")
public class PkbChecksManagementController {

  private final PkbChecksManagementService checksManagementService;

  @ApiPageable
  @GetMapping
  public ResponseEntity<Page<PkbCheckManagementDto>> getAllPkbChecks(@ApiIgnore Pageable pageable) {
    return ResponseEntity.ok(checksManagementService.getPagedAndSortedPkbChecks(pageable));
  }

  @PostMapping
  public ResponseEntity<PkbCheckManagementDto> addPkbCheck(
      @Valid @RequestBody PkbCheckManagementDto dto) {
    return ResponseEntity.ok(checksManagementService.upsertPkbCheck(dto));
  }

  @PutMapping
  public ResponseEntity<PkbCheckManagementDto> updatePkbCheck(
      @Valid @RequestBody PkbCheckManagementDto dto) {
    return ResponseEntity.ok(checksManagementService.upsertPkbCheck(dto));
  }

  @DeleteMapping("/{code}")
  public ResponseEntity<?> deletePkbCheck(@PathVariable("code") @NotBlank String code) {
    checksManagementService.deletePkbCheckByCode(code);
    return ResponseEntity.ok("Entity deleted");
  }

}
