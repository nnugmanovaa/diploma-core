package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FailedToSendSmsException extends RuntimeException {

  public FailedToSendSmsException(String message) {
    super(message);
  }
}

