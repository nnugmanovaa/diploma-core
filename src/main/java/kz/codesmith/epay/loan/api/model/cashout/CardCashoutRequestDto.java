package kz.codesmith.epay.loan.api.model.cashout;

import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.utils.Language;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardCashoutRequestDto {

  @NotNull
  private Integer orderId;

  @NotNull
  private Language language;

  private String backSuccessLink;

  private String backFailureLink;

}
