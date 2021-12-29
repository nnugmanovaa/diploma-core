package kz.codesmith.epay.loan.api;

import java.math.BigDecimal;
import java.math.BigInteger;
import kz.codesmith.epay.loan.api.component.SpringContext;
import kz.codesmith.epay.loan.api.payment.ws.LoanWsPaymentImpl;
import kz.integracia.Contract;
import kz.integracia.Payment;
import kz.integracia.PaymentApp;
import lombok.RequiredArgsConstructor;
import org.mockito.InjectMocks;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

@PrepareForTest(value = {SpringContext.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.*"})
@RequiredArgsConstructor
public class LoanWsPaymentImplTest extends PowerMockTestCase {

  Contract contract;
  Payment payment = new Payment();
  PaymentApp paymentApp = new PaymentApp();

  @InjectMocks
  LoanWsPaymentImpl loanWsPayment;

  public void initMocks() {
    PowerMockito.mockStatic(SpringContext.class);
  }

  public Contract createContract() {
    kz.integracia.Contract contract = new kz.integracia.Contract();
    contract.setAmountOfDebt(BigDecimal.valueOf(80000));
    contract.setPlannedPaymentAmount(BigDecimal.valueOf(20000));
    contract.setMinimumAmountOfPartialRepayment(BigDecimal.valueOf(40000));
    return contract;
  }

  @Test
  void paymentAmountIsLessThanPlannedSum() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(18000));
    payment.getData().setPayType(BigInteger.valueOf(2));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(1));
  }

  @Test
  void paymentAmountIsEqualToThePlannedSum() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(20000));
    payment.getData().setPayType(BigInteger.valueOf(2));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(1));
  }

  @Test
  void paymentAmountIsMoreThanPlannedSumAndLessThanPartialSum() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(30000));
    payment.getData().setPayType(BigInteger.valueOf(3));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(1));
  }

  @Test
  void paymentAmountIsEqualToThePartialSum() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(40000));
    payment.getData().setPayType(BigInteger.valueOf(1));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(2));
  }

  @Test
  void paymentAmountIsMoreThanPartialSumAndLessThanAmountOfDebt() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(60000));
    payment.getData().setPayType(BigInteger.valueOf(1));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(2));
  }

  @Test
  void paymentAmountIsEqualToTheAmountOfDebt() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(80000));
    payment.getData().setPayType(BigInteger.valueOf(1));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(3));
  }

  @Test
  void paymentAmountIsMoreThanAmountOfDebt() {
    initMocks();
    contract = createContract();
    payment.setData(paymentApp);
    payment.getData().setSum(BigDecimal.valueOf(85000));
    payment.getData().setPayType(BigInteger.valueOf(1));
    loanWsPayment.selectPayType(payment, contract);
    Assert.assertEquals(payment.getData().getPayType(), BigInteger.valueOf(3));
  }
}
