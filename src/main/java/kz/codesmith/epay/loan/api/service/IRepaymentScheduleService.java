package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;

public interface IRepaymentScheduleService {
  void getRepaymentSchedule(OrderEntity orderEntity);
}
