package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.service.impl.excel.ReportDto;

public interface IReportExcelService {
    byte[] getReport(ReportDto reportDto);
}
