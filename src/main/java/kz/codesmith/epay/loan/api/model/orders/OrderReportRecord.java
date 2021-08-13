package kz.codesmith.epay.loan.api.model.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderReportRecord {

  private Integer orderId;
  private LocalDate date;
  private String iin;
  private String msisdn;
  private String client;
  private BigDecimal loanAmount;
  private Integer loanPeriodMonths;
  private String loanProduct;
  private OrderState status;

}
