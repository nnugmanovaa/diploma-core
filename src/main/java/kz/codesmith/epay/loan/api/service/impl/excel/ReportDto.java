package kz.codesmith.epay.loan.api.service.impl.excel;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import lombok.Data;


@Data
public class ReportDto {

  @NotNull
  private LocalDate startDate;

  @NotNull
  private LocalDate endDate;

  private List<OrderState> states;

  private Integer orderId;

}
