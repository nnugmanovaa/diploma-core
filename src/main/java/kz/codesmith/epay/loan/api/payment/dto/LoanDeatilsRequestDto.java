package kz.codesmith.epay.loan.api.payment.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDeatilsRequestDto {
  @NotBlank
  private String iin;

  @NotBlank
  private String contractExtRefId;

  @NotNull
  private Integer orderId;
}
