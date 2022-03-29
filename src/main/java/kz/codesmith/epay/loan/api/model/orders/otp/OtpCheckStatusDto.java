package kz.codesmith.epay.loan.api.model.orders.otp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.OrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class OtpCheckStatusDto {

  @NotNull
  private OtpCheckStatus status;
  private String message;
  private OrderStatusDto orderStatus;
}
