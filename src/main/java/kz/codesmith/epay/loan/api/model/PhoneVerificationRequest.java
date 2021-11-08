package kz.codesmith.epay.loan.api.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneVerificationRequest {

  @NotNull
  @Pattern(regexp = "^77\\d{9}$", message = "{error.invalid-msisdn}")
  private String msisdn;
}
