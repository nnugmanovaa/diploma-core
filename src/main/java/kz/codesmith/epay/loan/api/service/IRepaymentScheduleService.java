package kz.codesmith.epay.loan.api.service;

import java.math.BigDecimal;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;

public interface IRepaymentScheduleService {
  void getRepaymentSchedule(OrderEntity orderEntity);

  void substractLoanRemainAmount(Integer paymentId, BigDecimal amount);
}
