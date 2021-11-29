package kz.codesmith.epay.loan.api.model.cashout;

import kz.codesmith.epay.core.shared.model.IdAndTimeKey;
import kz.codesmith.epay.loan.api.model.core.CoreOrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CoreInitResponseDto {
  private CoreOrderState state;
  private IdAndTimeKey order;
}
