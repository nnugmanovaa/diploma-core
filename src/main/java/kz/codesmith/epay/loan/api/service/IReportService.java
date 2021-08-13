package kz.codesmith.epay.loan.api.service;

import java.time.LocalDate;

public interface IReportService {
  byte[] getReport(LocalDate startDate, LocalDate endDate, Integer orderId);
}
