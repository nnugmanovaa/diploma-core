package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;

public interface IProcessAsync {
  void getRepaymentScheduleEvent(OrderEntity orderEntity);
}
