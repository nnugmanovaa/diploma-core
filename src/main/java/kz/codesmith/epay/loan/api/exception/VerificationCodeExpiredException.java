package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.GONE)
public class VerificationCodeExpiredException extends VerificationException {

  private static final String ERROR_CODE = "VerificationCodeExpired";
  private String message;

  public VerificationCodeExpiredException(String message) {
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.GONE;
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}

