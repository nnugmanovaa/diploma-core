package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class WrongVerificationCodeException extends VerificationException {

  private static final String ERROR_CODE = "WrongVerificationCode";
  private final String message;

  public WrongVerificationCodeException(String message) {
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.FORBIDDEN;
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}

