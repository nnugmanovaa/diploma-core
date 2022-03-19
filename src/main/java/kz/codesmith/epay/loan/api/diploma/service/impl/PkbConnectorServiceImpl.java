package kz.codesmith.epay.loan.api.diploma.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kz.codesmith.epay.loan.api.configuration.PkbConnectorProperties;
import kz.codesmith.epay.loan.api.diploma.KdnParser;
import kz.codesmith.epay.loan.api.diploma.OverduesService;
import kz.codesmith.epay.loan.api.diploma.StandardReportResultParser;
import kz.codesmith.epay.loan.api.diploma.model.CigResult;
import kz.codesmith.epay.loan.api.diploma.model.Contract;
import kz.codesmith.epay.loan.api.diploma.model.OverduePayment;
import kz.codesmith.epay.loan.api.diploma.model.ScoringModel;
import kz.codesmith.epay.loan.api.diploma.model.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.diploma.service.IPkbConnectorService;
import kz.codesmith.epay.loan.api.model.exception.PkbConnectorReportsFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PkbConnectorServiceImpl implements IPkbConnectorService {
  private final RestTemplate restTemplate;
  private final PkbConnectorProperties pkbConnectorProperties;

  @Override
  public ApplicationReport parseKdnReport(String iin) {
    var url = pkbConnectorProperties.getUrl() + "/pkb/kdn/" + iin + "/raw";
    var requestEntity = HttpEntity.EMPTY;

    try {
      var response = restTemplate
          .exchange(url, HttpMethod.GET, requestEntity, String.class);
      if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
        return KdnParser.parse(response.getBody());
      }
      throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");
    } catch (Exception e) {
      log.error("Failed to get pkb reports from pkb-connector");
      throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");
    }
  }

  @Override
  public CigResult parseExtendedReport(String iin) {
    var url = pkbConnectorProperties.getUrl() + "/pkb/extended/" + iin + "/raw";
    var requestEntity = HttpEntity.EMPTY;

    try {
      var response = restTemplate
          .exchange(url, HttpMethod.GET, requestEntity, String.class);
      if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
        return StandardReportResultParser.parse(response.getBody());
      }
      throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");
    } catch (Exception e) {
      log.error("Failed to get pkb reports from pkb-connector");
      throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");

    }
  }

  @Override
  public ScoringModel getScoringModelByIin(String iin) {
    ApplicationReport kdnReport = parseKdnReport(iin);
    CigResult extendedReport = parseExtendedReport(iin);
    List<OverduePayment> overduePayments = OverduesService.getAllOverduePayments(extendedReport);
    List<Double> penalties = new ArrayList<>();
    overduePayments.forEach(overdue -> {
      String fine = overdue.getFine()
          .replace(" KZT", "")
          .replace(",", ".")
          .replace("-", "0")
          .replace(" ", "");
      String penalty = overdue.getPenalty()
          .replace(" KZT", "")
          .replace(",", ".")
          .replace("-", "0")
          .replace(" ", "");
      penalties.add(Double.valueOf(fine) + Double.valueOf(penalty));
    });
    List<Integer> overdueDays = overduePayments
        .stream()
        .map(OverduePayment::getOverdueDays)
        .collect(Collectors.toList());
    List<Contract> contracts = extendedReport
        .getResult()
        .getRoot()
        .getExistingContracts()
        .getContracts();
    return ScoringModel.builder()
        .iin(iin)
        .debt(kdnReport.getDebt())
        .income(kdnReport.getIncome())
        .numberOfActiveLoans(contracts != null ? contracts.size() : 0)
        .maxOverdueAmount(penalties.size() != 0 ? Collections.max(penalties) : 0d)
        .overduesSum(penalties
            .stream()
            .mapToDouble(el -> el).sum())
        .maxOverdueDays(overdueDays.size() != 0 ? Collections.max(overdueDays) : 0)
        .build();
  }
}
