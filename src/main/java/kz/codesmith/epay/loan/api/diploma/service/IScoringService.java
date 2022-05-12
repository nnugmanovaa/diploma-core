package kz.codesmith.epay.loan.api.diploma.service;

import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.diploma.model.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.diploma.model.ScoringRequest;
import kz.codesmith.epay.loan.api.diploma.model.ScoringResponse;
import kz.codesmith.epay.loan.api.diploma.model.payout.PayoutUpdateStateEventDto;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.OrderSummaryDto;

public interface IScoringService {
  ScoringResponse score(ScoringRequest request);

  LoanCheckAccountResponse getAccountLoans(LoanCheckAccountRequestDto requestDto);

  AcquiringPaymentResponse initPayment(LoanPaymentRequestDto requestDto);

  OrderSummaryDto getOrderStatus(PayoutUpdateStateEventDto updateStateEventDto);
}
