package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class VerificationRequestNotFoundException extends VerificationException {
  private static final String ERROR_CODE = "VerificationRequestNotFound";
  private String message;

  public VerificationRequestNotFoundException(String message) {
    this.message = message;
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.NOT_FOUND;
  }

  @Override
  public String getErrorCode() {
    return ERROR_CODE;
  }
}
