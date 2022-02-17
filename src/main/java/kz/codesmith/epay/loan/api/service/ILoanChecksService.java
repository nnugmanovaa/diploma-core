package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.model.CheckResult;

public interface ILoanChecksService {

  CheckResult stopFactorCheck(String iin);

  CheckResult stopFactorLocalCheck(String iin);

  String stopFactorCheckRaw(String iin);

}
