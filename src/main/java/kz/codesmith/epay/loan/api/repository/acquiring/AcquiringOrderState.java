package kz.codesmith.epay.loan.api.repository.acquiring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum AcquiringOrderState {
  NEW("Новый платёж"),
  SUCCESS("Успешный платёж"),
  IN_3DS("Платёж в процессе 3DS сессии"),
  REFUND("Частичный возврат"),
  REVERSE("Полный возврат"),
  ERROR("Ошибка при проведении платежа"),
  TIMED_OUT("Timeout при проведении платежа"),
  WAIT_CVC("В ожидании CVC-кода"),
  AUTH_OK("Сумма успешно авторизована"),
  EXPIRED("Истёк срок проведения платежа"),
  IN_PROCESS("В процессе");
  private String description;
}
