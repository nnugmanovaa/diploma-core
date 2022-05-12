package kz.codesmith.epay.loan.api.diploma.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum LoanStatus {
  PENDING("Погасить"),
  PAID("Оплачен"),
  ACTIVE("Активный"),
  EXPIRED("Просрочено");

  private String description;
}
