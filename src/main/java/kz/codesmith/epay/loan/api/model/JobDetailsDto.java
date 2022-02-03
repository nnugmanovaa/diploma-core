package kz.codesmith.epay.loan.api.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobDetailsDto {
  private String education;
  private String employment;
  private String typeOfWork;
  private String workPosition;
  private String employer;
  private BigDecimal monthlyIncome;
  private BigDecimal additionalMonthlyIncome;
  private String maritalStatus;
  private Integer numberOfKids;
}
