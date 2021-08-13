package kz.codesmith.epay.loan.api.payment.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanCheckAccountRequestDto {
  @NotBlank
  private String iin;
}
