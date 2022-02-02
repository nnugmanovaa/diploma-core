package kz.codesmith.epay.loan.api.service.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import kz.codesmith.epay.loan.api.domain.RepaymentScheduleEntity;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.map.RepaymentScheduleMapper;
import kz.codesmith.epay.loan.api.model.schedule.RepaymentScheduleDto;
import kz.codesmith.epay.loan.api.model.schedule.ScheduleItemsDto;
import kz.codesmith.epay.loan.api.repository.RepaymentScheduleRepository;
import kz.codesmith.epay.loan.api.repository.ScheduleItemsRepository;
import kz.codesmith.epay.loan.api.service.IRepaymentScheduleService;
import kz.payintech.DataRowLoanSchedule;
import kz.payintech.ListLoanMethod;
import kz.payintech.ResultResponsegetLoanScheduleCalculation;
import kz.payintech.siteexchange.SiteExchangePortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentScheduleServiceImpl implements IRepaymentScheduleService {
  private final SiteExchangePortType wsMfoCoreService;
  private final RepaymentScheduleRepository repaymentScheduleRepository;
  private final ScheduleItemsRepository scheduleItemsRepository;
  private final RepaymentScheduleMapper scheduleMapper;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public void getRepaymentSchedule(OrderEntity order) {
    var response = wsMfoCoreService.getLoanScheduleCalculation(
        order.getLoanAmount(),
        BigInteger.valueOf(order.getLoanPeriodMonths()),
        order.getLoanInterestRate().floatValue(),
        order.getLoanProduct(),
        ListLoanMethod.valueOf(order.getLoanMethod()),
        order.getContractExtRefTime().toLocalDate()
    );

    if (Objects.nonNull(response) && StringUtils.isNotBlank(response.getError())) {
      throw new MfoGeneralApiException(response.getError());
    } else {
      RepaymentScheduleDto repaymentScheduleDto = fillRepaymentSchedule(order, response);

      RepaymentScheduleEntity repaymentScheduleEntity = repaymentScheduleRepository
          .save(scheduleMapper
              .toEntity(repaymentScheduleDto));
      List<ScheduleItemsDto> scheduleItems = new ArrayList<>();
      for (DataRowLoanSchedule data: response.getData().getRowLoanSchedule()) {
        scheduleItems
            .add(mapToScheduleItems(data, repaymentScheduleEntity.getRepaymentScheduleId()));
      }
      scheduleItemsRepository.saveAll(scheduleMapper.toItemsEntityList(scheduleItems));
    }
  }

  private RepaymentScheduleDto fillRepaymentSchedule(OrderEntity order,
      ResultResponsegetLoanScheduleCalculation response) {
    return RepaymentScheduleDto.builder()
        .amountOverpayment(response.getData().getAmountOverpayment())
        .amountRemain(response.getData().getTotalAmountToBePaid())
        .orderId(order.getOrderId())
        .build();
  }


  private ScheduleItemsDto mapToScheduleItems(DataRowLoanSchedule schedule, Integer id) {
    return ScheduleItemsDto.builder()
        .repaymentScheduleId(id)
        .number(schedule.getNumber().intValue())
        .amountToBePaid(schedule.getAmountToBePaid())
        .paymentDate(String.valueOf(schedule.getPaymentDate()))
        .reward(schedule.getReward())
        .totalAmountDebt(schedule.getTotalAmountDebt())
        .build();
  }
}
