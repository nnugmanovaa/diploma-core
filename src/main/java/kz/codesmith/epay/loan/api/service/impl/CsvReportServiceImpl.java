package kz.codesmith.epay.loan.api.service.impl;

import java.time.LocalDate;
import kz.codesmith.epay.core.shared.model.csv.CsvMeta;
import kz.codesmith.epay.core.shared.model.csv.CsvUtils;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderReportRecord;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.supercsv.cellprocessor.ift.CellProcessor;


@Component("csvReportService")
@RequiredArgsConstructor
public class CsvReportServiceImpl implements IReportService {

  private final ILoanOrdersService ordersServices;

  public static final CsvMeta ordersCsvMeta = new CsvMeta(
      new String[]{"orderId", "date", "iin", "msisdn", "client",
          "loanAmount", "loanPeriodMonths", "loanProduct", "status"},
      new String[]{"Номер заявки", "Дата", "ИИН", "Номер телефона", "Клиент",
          "Запрашиваемая сумма", "Период", "Кредитный продукт", "Статус"},
      new CellProcessor[]{
          new org.supercsv.cellprocessor.Optional(), // orderId
          new org.supercsv.cellprocessor.Optional(), // date
          new org.supercsv.cellprocessor.Optional(), // iin
          new org.supercsv.cellprocessor.Optional(), // msisdn
          new org.supercsv.cellprocessor.Optional(), // client
          new org.supercsv.cellprocessor.Optional(), // loanAmount
          new org.supercsv.cellprocessor.Optional(), // loanPeriodMonths
          new org.supercsv.cellprocessor.Optional(), // loanProduct
          new org.supercsv.cellprocessor.Optional(), // status
      }
  );

  @Override
  public byte[] getReport(LocalDate startDate, LocalDate endDate, Integer orderId) {
    return CsvUtils.mapToByteArray(
        ordersServices.getOrdersByUserOwner(
            startDate,
            endDate,
            orderId,
            Pageable.unpaged()
        ).map(this::getRecord)
            .toList(),
        ordersCsvMeta
    );
  }

  private OrderReportRecord getRecord(OrderDto orderDto) {
    return OrderReportRecord.builder()
        .orderId(orderDto.getOrderId())
        .date(orderDto.getInsertedTime().toLocalDate())
        .iin(orderDto.getIin())
        .msisdn(orderDto.getMsisdn())
        .client(orderDto.getClientInfo())
        .loanAmount(orderDto.getLoanAmount())
        .loanPeriodMonths(orderDto.getLoanPeriodMonths())
        .loanProduct(orderDto.getLoanProduct())
        .status(orderDto.getStatus())
        .build();
  }

}
