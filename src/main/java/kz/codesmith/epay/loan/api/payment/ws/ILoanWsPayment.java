package kz.codesmith.epay.loan.api.payment.ws;

import kz.integracia.Payment;
import kz.integracia.Response;

public interface ILoanWsPayment {
  Response processPayment(Payment paymentApp);

  Response checkAccount(Payment paymentApp);
}
