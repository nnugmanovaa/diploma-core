package kz.codesmith.epay.loan.api.service;

import java.time.LocalDateTime;
import kz.codesmith.epay.telegram.gw.models.dto.LoanPaymentAnalyzerDto;

public interface IPaymentAnalyzerService {

  LoanPaymentAnalyzerDto analyzePaymentsBetween(LocalDateTime startTime, LocalDateTime endTime);
}
