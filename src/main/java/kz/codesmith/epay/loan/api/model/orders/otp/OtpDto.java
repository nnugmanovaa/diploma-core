package kz.codesmith.epay.loan.api.model.orders.otp;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpDto {

  private String code;
  private Date generated;
  private boolean onceChecked;
  private Integer sendCounter;
}
