package kz.codesmith.epay.loan.api.model.scoring;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class AlternativeLoanParams {

  private final BigDecimal income;
  private final BigDecimal debt;

}
