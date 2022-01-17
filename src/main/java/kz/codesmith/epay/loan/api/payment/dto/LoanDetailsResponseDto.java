package kz.codesmith.epay.loan.api.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDetailsResponseDto {
  private LoanInfoDto loanInfoDto;
}
