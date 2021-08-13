package kz.codesmith.epay.loan.api.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class SignInFailedException extends RuntimeException {

  public SignInFailedException(String message) {
    super(message);
  }

}

