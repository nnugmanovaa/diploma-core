package kz.codesmith.epay.loan.api.payment.ws;

import kz.integracia.Payment;
import kz.integracia.PaymentResponse;
import kz.integracia.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


@Endpoint
@RequiredArgsConstructor
public class LoanPaymentEndpoint {
  private static final String NAMESPACE_URI = "http://www.integracia.kz";

  private final ILoanWsPayment loanPaymentWs;

  @PayloadRoot(namespace = NAMESPACE_URI, localPart = "payment")
  @ResponsePayload
  public PaymentResponse processPayment(@RequestPayload Payment request) {
    Response response;
    if (request.getData().getCommand().equals("pay")) {
      response = loanPaymentWs.processPayment(request);
    } else {
      response = loanPaymentWs.checkAccount(request);
    }
    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setReturn(response);
    return paymentResponse;
  }
}
