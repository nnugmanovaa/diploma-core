package kz.codesmith.epay.loan.api.payment.ws;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.cashout.PaymentAppEntityEventDto;
import kz.codesmith.epay.loan.api.payment.LoanPaymentConstants;
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
import org.apache.commons.lang3.StringUtils;
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
  private final MfsCoreProperties mfsCoreProperties;

  /** Actual services' IDs for MFO paymetn types in core-api-server used for return remain amount.
   * */
  public Map<String, Integer> coreServiceMappings = new HashMap();

  public static final String SUCCESS_RESPONSE_RESULT = "0";

  @PostConstruct
  public void createServiceMappings() {
    coreServiceMappings.put("1", Integer.valueOf(mfsCoreProperties.getRegularServiceId()));
    coreServiceMappings.put("2", Integer.valueOf(mfsCoreProperties.getEarlyPartialServiceId()));
    coreServiceMappings.put("3", Integer.valueOf(mfsCoreProperties.getEarlyFullServiceId()));
  }

  @Transactional
  @Override
  public Response processPayment(Payment paymentApp) {
    paymentApp.getData().setCommand(LoanPaymentConstants.TYPE_CHECK);
    kz.integracia.Response checkResponse = checkAccount(paymentApp);
    Contract checkedContract = checkResponse.getContracts().getContract().stream()
        .filter(contract -> contract.getContractNumber()
            .equals(paymentApp.getData().getContractNumber()))
        .findFirst().get();
    var serviceId = coreServiceMappings.get(String.valueOf(paymentApp.getData()
        .getPayType()));
    BigDecimal expectedAmount = paymentApp.getData().getSum();
    if (checkedContract.getAmountOfDebt().compareTo(paymentApp.getData().getSum()) != 1) {
      paymentApp.getData().setSum(checkedContract.getAmountOfDebt());
      paymentApp.getData().setPayType(new BigInteger("3"));
    }
    PaymentEntity paymentEntity = paymentService.startNewPayment(paymentApp);
    messageService.fireLoanPaymentEvent(LoanWsPaymentDto.builder()
        .paymentApp(paymentApp.getData())
        .paymentId(paymentEntity.getPaymentId())
        .contractDate(String.valueOf(paymentApp.getData().getContractDate()))
        .build(),
        AmqpConfig.LOAN_PAYMENT_ROUTING_KEY);
    messageService
        .fireLoanStatusGetEvent(PaymentAppEntityEventDto.builder()
             .iin(paymentApp
                 .getData()
                 .getAccount())
            .extRefId(StringUtils.EMPTY)
            .payment(paymentApp)
            .contract(checkedContract)
            .serviceId(serviceId)
            .expectedAmount(expectedAmount)
            .build(),
            AmqpConfig.LOAN_STATUSES_ROUTING_KEY);
    paymentEntity.setInitPaymentStatus(AcquiringBaseStatus.SUCCESS);
    return fillResponse(paymentApp.getData().getAccount(),
        String.valueOf(paymentEntity.getPaymentId()));
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

  private Response fillResponse(String account, String extRefId) {
    Response response = new Response();
    response.setAccount(account);
    response.setComment("Платеж принят");
    response.setResult(SUCCESS_RESPONSE_RESULT);
    response.setOsmpTxnId(extRefId);
    return response;
  }
}
