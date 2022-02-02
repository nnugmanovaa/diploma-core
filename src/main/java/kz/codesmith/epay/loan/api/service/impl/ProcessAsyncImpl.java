package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.service.IProcessAsync;
import kz.codesmith.epay.loan.api.service.IRepaymentScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Async
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessAsyncImpl implements IProcessAsync {
  private final IRepaymentScheduleService scheduleService;

  @Override
  public void getRepaymentScheduleEvent(OrderEntity order) {
    scheduleService.getRepaymentSchedule(order);
  }
}
