package kz.codesmith.epay.loan.api.payment;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;

public interface ILoanPayment {
  LoanCheckAccountResponse getAccountLoans(LoanCheckAccountRequestDto requestDto);

  AcquiringPaymentResponse initPayment(LoanPaymentRequestDto requestDto);

  LoanPaymentResponseDto processPayment(LoanPaymentRequestDto requestDto);

  LoanPaymentResponseDto retryPayment(Integer paymentId);
}
