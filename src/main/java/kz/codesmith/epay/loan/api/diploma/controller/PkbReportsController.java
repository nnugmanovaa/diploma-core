package kz.codesmith.epay.loan.api.diploma.controller;

import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.diploma.model.CigResult;
import kz.codesmith.epay.loan.api.diploma.model.ScoringModel;
import kz.codesmith.epay.loan.api.diploma.model.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.diploma.service.IPkbConnectorService;
import kz.codesmith.logger.Logged;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Logged
@RestController
@RequestMapping("/pkb")
@RequiredArgsConstructor
public class PkbReportsController {
  private final IPkbConnectorService pkbConnectorService;

  @GetMapping("/kdn/{iin}")
  public ResponseEntity<ApplicationReport> getKdnReport
      (@PathVariable("iin") @NotNull String iin) {
    return ResponseEntity.ok(pkbConnectorService.parseKdnReport(iin));
  }

  @GetMapping("/extended/{iin}")
  public ResponseEntity<CigResult> getExtendedReport(
      @PathVariable("iin") @NotNull String iin) {
    return ResponseEntity.ok(pkbConnectorService.parseExtendedReport(iin));
  }

  @GetMapping("/scoring-model/{iin}")
  public ResponseEntity<ScoringModel> getScoringModelByIin(
      @PathVariable("iin") @NotNull String iin) {
    return ResponseEntity.ok(pkbConnectorService.getScoringModelByIin(iin));
  }
}
