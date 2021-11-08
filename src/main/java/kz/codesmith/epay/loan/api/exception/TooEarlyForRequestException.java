package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooEarlyForRequestException extends VerificationException {

  private static final String ERROR_CODE = "TooEarlyForRequest";
  private final String message;
  private int currentTimeLimit;

  public TooEarlyForRequestException(String message, int currentTimeLimit) {
    this.message = message;
    this.currentTimeLimit = currentTimeLimit;
  }


  @Override
  public HttpStatus getStatus() {
    return HttpStatus.TOO_MANY_REQUESTS;
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
