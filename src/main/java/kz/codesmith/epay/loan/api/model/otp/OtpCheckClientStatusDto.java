package kz.codesmith.epay.loan.api.model.otp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.orders.otp.OtpCheckStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class OtpCheckClientStatusDto {

  @NotNull
  private OtpCheckStatus status;

  private String message;

}
