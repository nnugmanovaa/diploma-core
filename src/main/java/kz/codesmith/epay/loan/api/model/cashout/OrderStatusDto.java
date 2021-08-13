package kz.codesmith.epay.loan.api.model.cashout;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class OrderStatusDto {

  private String state;
  private OrderDto order;
  private String description;

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class OrderDto {

    private Integer id;
    private String time;
  }
}