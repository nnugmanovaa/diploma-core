package kz.codesmith.epay.loan.api.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.controller.qualifier.ApiPageable;
import kz.codesmith.epay.loan.api.model.ScoringVarDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVarType;
import kz.codesmith.epay.loan.api.service.impl.ScoringVarsService;
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
@RequestMapping("/scoring-vars")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('MFO_ADMIN')")
public class ScoringVarsController {

  private final ScoringVarsService scoringVarsService;

  @ApiPageable
  @GetMapping
  public ResponseEntity<Page<ScoringVarDto>> getAllScoringVars(@ApiIgnore Pageable pageable) {
    return ResponseEntity.ok(scoringVarsService.getAllScoringVars(pageable));
  }

  @PostMapping
  public ResponseEntity<ScoringVarDto> addScoringVar(@Valid @RequestBody ScoringVarDto dto) {
    return ResponseEntity.ok(scoringVarsService.upsertScoringVar(dto));
  }

  @PutMapping
  public ResponseEntity<ScoringVarDto> updateScoringVar(@Valid @RequestBody ScoringVarDto dto) {
    return ResponseEntity.ok(scoringVarsService.upsertScoringVar(dto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteScoringVar(@PathVariable("id") @NotBlank Integer id) {
    scoringVarsService.deleteScoringVar(id);
    return ResponseEntity.ok("Entity deleted");
  }

  @GetMapping("/types")
  public ResponseEntity<?> getScoringVarsTypes() {
    return ResponseEntity.ok(ScoringVarType.values());
  }

}
