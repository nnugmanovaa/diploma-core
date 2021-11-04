package kz.codesmith.epay.loan.api.service;

import java.time.LocalDate;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderState;


public interface IReportExcelService {
    byte[] getReport(LocalDate startDate, LocalDate endDate, Integer orderId,
        List<OrderState> state);
}
