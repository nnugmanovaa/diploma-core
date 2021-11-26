package kz.codesmith.epay.loan.api.model.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum CoreOrderState {
  NEW("Новый"),
  SUCCESS("Успешный"),
  EXECUTING("В процессе"),
  EXPIRED("Истек"),
  ERROR("Ошибка"),
  FIN_APRV("Одобрение финансиста"),
  RVK_FULL("Полный возврат"),
  RVK_PART("Частичный возврат"),
  BANK_PROC("Обработать в банке"),
  MERCH_PROC("Обработка у поставщика");

  private String description;

}
