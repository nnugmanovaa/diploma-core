package kz.codesmith.epay.loan.api.service.impl.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderReportRecord;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IReportExcelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;


@Component("excelReportService")
@RequiredArgsConstructor
public class ExcelReportServiceImpl implements IReportExcelService {

  private final ILoanOrdersService ordersServices;

  @Override
  public byte[] getReport(LocalDate startDate, LocalDate endDate, Integer orderId,
      List<OrderState> states) {
    List<OrderDto> orderDtos = ordersServices.getOrdersByUserOwner(
        startDate,
        endDate,
        orderId,
        states,
        Pageable.unpaged()
    ).getContent();
    List<OrderReportRecord> orderReports = orderDtos
        .stream()
        .map(this::getRecord)
        .collect(Collectors.toList());
    byte[] result = ExcelHelper.ordersToExcel(orderReports);
    return result;
  }

  private OrderReportRecord getRecord(OrderDto orderDto) {
    ObjectMapper objectMapper = new ObjectMapper();
    return OrderReportRecord.builder()
        .orderId(orderDto.getOrderId())
        .status(orderDto.getStatus())
        .insertedTime(orderDto.getInsertedTime())
        .updatedTime(orderDto.getUpdatedTime())
        .client(orderDto.getClientInfo())
        .msisdn(orderDto.getMsisdn())
        .iin(orderDto.getIin())
        .loanAmount(orderDto.getLoanAmount())
        .loanPeriodMonths(orderDto.getLoanPeriodMonths())
        .loanProduct(orderDto.getLoanProduct())
        .personalInfoDto(objectMapper
            .convertValue(orderDto.getPersonalInfo(), PersonalInfoDto.class))
        .scoringInfo(objectMapper
            .convertValue(orderDto.getScoringInfo(), ScoringInfo.class))
        .build();

  }
}
