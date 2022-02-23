package kz.codesmith.epay.loan.api.service;

import com.creditinfo.ws.score.ScoreCard;
import com.creditinfo.ws.score.ScoreData;
import com.fcb.closedcontracts.service.web.ServiceReturn;
import java.time.LocalDate;
import java.util.List;
import kz.codesmith.epay.loan.api.model.PkbReportsDto;
import kz.codesmith.epay.loan.api.model.PkbReportsRequest;
import kz.codesmith.epay.loan.api.model.pkb.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest;
import kz.codesmith.epay.loan.api.model.pkb.ws.ApplicationReportDto;
import kz.com.fcb.fico.Result;

public interface IPkbScoreService {

  List<ScoreCard> getScoreCards();

  ScoreData getBehaviorScore(String iin, boolean consentConfirmed);

  Result getFicoScore(String iin);

  ApplicationReportDto getKdnScore(
      String iin,
      LocalDate birthDate,
      boolean consentConfirmed,
      String firstName,
      String lastName,
      String middleName
  );

  ServiceReturn getContractSum(String iin, boolean consentConfirmed);

  String getCustomerInfoByIin(String iin);

  ApplicationReport getKdnReport(KdnRequest kdnRequest);

  PkbReportsDto getAllPkbReports(PkbReportsRequest request);
}
