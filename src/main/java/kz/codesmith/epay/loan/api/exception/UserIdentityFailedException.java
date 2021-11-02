package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class UserIdentityFailedException extends VerificationException {

  private static final String ERROR_CODE = "UserIdentityFailed";
  private final String message;

  public UserIdentityFailedException(String message) {
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
