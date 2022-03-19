package kz.codesmith.epay.loan.api.diploma.service;

import kz.codesmith.epay.loan.api.diploma.model.CigResult;
import kz.codesmith.epay.loan.api.diploma.model.ScoringModel;
import kz.codesmith.epay.loan.api.diploma.model.kdn.ApplicationReport;

public interface IPkbConnectorService {
  ApplicationReport parseKdnReport(String iin);

  CigResult parseExtendedReport(String iin);

  ScoringModel getScoringModelByIin(String iin);
}
