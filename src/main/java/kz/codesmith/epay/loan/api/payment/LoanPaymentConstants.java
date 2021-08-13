package kz.codesmith.epay.loan.api.payment;

import java.util.List;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;

public class LoanPaymentConstants {
  public static final String TYPE_CHECK = "check";
  public static final String TYPE_PAY = "pay";
  public static final String SUCCESS_RESPONSE_RESULT = "0";
  public static final String NO_ACTIVE_LOANS = "Нет активных займов";
  public static final String LOANS_LIST = "Активные займы";
  public static final String CANNOT_GET_LOANS = "Не удалось получить список активных займов";
  public static final String KZT_PAYMENT_CURRENCY = "KZT";
  public static final String MESSAGE_UNEXPECTED_BEHAVIOUR = "unexpected behaviour";
  public static final String MESSAGE_DEFAULT_EXCEPTION = "an exception occurred";
  public static final String MESSAGE_PAYMENT_ALREADY_PROCESSED = "Payment was already processed";
  public static final String MESSAGE_BAD_STATUS_RESPONSE = "Bad status response";

  public static final String COULD_NOT_CREATE_REQUEST = "Could not create payment request";
  public static final String LOAN_PAYMENT_INIT_DESCRIPTION = "Платёж по займу";

  public static final List<AcquiringOrderState> ACQUIRING_ERROR_STATUSES = List
      .of(AcquiringOrderState.ERROR, AcquiringOrderState.TIMED_OUT, AcquiringOrderState.EXPIRED);

  private LoanPaymentConstants() {
  }
}
