package kz.codesmith.epay.loan.api.service;

import java.math.BigDecimal;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.payintech.InformationTheAgreement;
import kz.payintech.ListLoanMethod;
import kz.payintech.LoanSchedule;
import kz.payintech.NewOrder;
import kz.payintech.ResultDataNumberDate;
import kz.payintech.ResultDataPDF;
import kz.payintech.SummPaymentSchedule;
import kz.payintech.TotalDebt;
import kz.payintech.UserOrderList;


public interface IMfoCoreService {

  List<SummPaymentSchedule> getSummPaymentSchedule(String iin);

  ResultDataPDF getNewContract(OrderDto order);

  ResultDataPDF getNewContract(
      ResultDataNumberDate dataNumberDate,
      float loanInterestRate
  );

  List<UserOrderList> getUserOrderList(String iin);

  TotalDebt getTotalDebt(String iin);

  LoanSchedule getLoanScheduleCalculation(
      BigDecimal amount,
      Integer loanMonthPeriod,
      float loanInterestRate,
      String creditProduct,
      ListLoanMethod loanType
  );

  List<InformationTheAgreement> getUserContractsList(String iin);

  void sendBorrowerSignature(OrderDto order);

  void sendBorrowerSignature(ResultDataNumberDate dataNumberDate);

  ResultDataNumberDate getNewOrder(NewOrder newOrder);

  ResultDataNumberDate getNewOrder(OrderDto order);
}
