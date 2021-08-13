package kz.codesmith.epay.loan.api.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.service.IPaymentAnalyzerService;
import kz.codesmith.epay.telegram.gw.models.dto.LoanPaymentAnalyzerDto;
import kz.codesmith.epay.telegram.gw.models.dto.TelegramLoanPaymentDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAnalyzerServiceImpl implements IPaymentAnalyzerService {

  private final PaymentRepository paymentRepository;

  @Override
  public LoanPaymentAnalyzerDto analyzePaymentsBetween(LocalDateTime startTime,
      LocalDateTime endTime) {
    if (endTime.isBefore(startTime)) {
      throw new GeneralApiServerException(ApiErrorType.E500_INVALID_DATE);
    }

    List<Object[]> payments = paymentRepository
        .findPaymentIdByOrderTimeAndProcessingStatus(startTime, endTime,
            MfoProcessingStatus.ERROR_IN_PROCESSING);

    List<TelegramLoanPaymentDto> outPayments = new ArrayList<>();

    for (Object[] objects : payments) {
      outPayments.add(TelegramLoanPaymentDto.builder()
          .paymentId((Integer) objects[0])
          .mfoProcessingMessage((String) objects[1])
          .build());
    }

    return LoanPaymentAnalyzerDto.builder()
        .payments(outPayments)
        .startTime(startTime)
        .endTime(endTime)
        .build();

  }
}
