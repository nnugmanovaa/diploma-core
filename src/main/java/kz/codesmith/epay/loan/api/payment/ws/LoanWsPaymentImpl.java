package kz.codesmith.epay.loan.api.payment.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Objects;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.codesmith.epay.loan.api.service.impl.MessageService;
import kz.integracia.Contract;
import kz.integracia.Contracts;
import kz.integracia.Payment;
import kz.integracia.Response;
import kz.pitech.mfo.PaymentApp;
import kz.pitech.mfo.PaymentServicesPortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanWsPaymentImpl implements ILoanWsPayment {
  private final IPaymentService paymentService;
  private final MessageService messageService;
  private final PaymentServicesPortType servicesPortType;
  private final ModelMapper mapper;

  public static final String SUCCESS_RESPONSE_RESULT = "0";

  @Transactional
  @Override
  public Response processPayment(Payment paymentApp) {
    PaymentEntity paymentEntity = paymentService.startNewPayment(paymentApp);
    messageService.fireLoanPaymentEvent(LoanWsPaymentDto.builder()
        .paymentApp(paymentApp.getData())
        .paymentId(paymentEntity.getPaymentId())
        .contractDate(String.valueOf(paymentApp.getData().getContractDate()))
        .build(),
        AmqpConfig.LOAN_PAYMENT_ROUTING_KEY);
    messageService
        .fireLoanStatusGetEvent(paymentApp
                .getData()
                .getAccount(),
            AmqpConfig.LOAN_STATUSES_ROUTING_KEY);
    paymentEntity.setInitPaymentStatus(AcquiringBaseStatus.SUCCESS);
    return fillResponse(paymentApp.getData().getAccount());
  }

  @Override
  public Response checkAccount(Payment paymentApp) {
    PaymentApp app = mapper
        .map(paymentApp.getData(), PaymentApp.class);

    kz.pitech.mfo.Response response = servicesPortType.payment(app);
    Response result = mapper
        .map(response, Response.class);
    Contracts contracts = new Contracts();
    if (Objects.nonNull(response.getContracts())
        && Objects.nonNull(response.getContracts().getContract())) {
      response.getContracts()
          .getContract()
          .forEach(contract -> {
            contracts
                .getContract()
                .add(fillContract(contract));
          });
      result.setContracts(contracts);
    }

    return result;
  }

  private Contract fillContract(kz.pitech.mfo.Contract contract) {
    Contract result = new Contract();
    result.setAmountOfDebt(contract.getAmountOfDebt());
    result.setContractDate(contract.getContractDate());
    result.setContractNumber(contract.getContractNumber());
    result.setArrears(contract.getArrears());
    result.setPlannedPaymentAmount(contract.getPlannedPaymentAmount());
    result.setMinimumAmountOfPartialRepayment(contract.getMinimumAmountOfPartialRepayment());
    result.setPlannedPaymentDate(contract.getPlannedPaymentDate());
    return result;
  }

  private Response fillResponse(String account) {
    Response response = new Response();
    response.setAccount(account);
    response.setComment("Платеж принят");
    response.setResult(SUCCESS_RESPONSE_RESULT);
    return response;
  }
}
