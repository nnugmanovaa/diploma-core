package kz.codesmith.epay.loan.api.diploma.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanCheckAccountResponse {
  private String message;
  private List<LoanInfoDto> loans;
}
