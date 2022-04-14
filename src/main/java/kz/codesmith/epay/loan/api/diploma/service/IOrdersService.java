package kz.codesmith.epay.loan.api.diploma.service;

import java.time.LocalDate;
import java.util.List;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrdersService {
  Page<OrderDto> getOrdersByUserOwner(
      LocalDate startDate,
      LocalDate endDate,
      Integer orderId,
      List<OrderState> states,
      Pageable pageRequest
  );
}
