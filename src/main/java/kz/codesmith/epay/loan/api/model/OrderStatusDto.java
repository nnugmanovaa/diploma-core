package kz.codesmith.epay.loan.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import java.io.Serializable;
import kz.codesmith.epay.core.shared.model.IdAndTimeKey;
import kz.codesmith.epay.core.shared.views.Views;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
@JsonView(Views.Public.class)
public class OrderStatusDto implements Serializable {

  private OrderState state;
  private IdAndTimeKey order;
  private String description;
}
