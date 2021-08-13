package kz.codesmith.epay.loan.api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum IncomeInfoType {
  EDUCATION("Образование"),
  EMPLOYMENT("Занятость"),
  EMPLOYMENT_TYPE("Вид деятельности"),
  EMPLOYMENT_POSITION("Занимаемая должность"),
  MARITAL_STATUS("Семейное положение"),
  NUMBER_OF_KIDS("Количество детей"),
  PERIOD_OF_RESIDENCY("Период проживания");

  private String description;

}
