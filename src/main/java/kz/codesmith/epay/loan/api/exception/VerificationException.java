package kz.codesmith.epay.loan.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public abstract class VerificationException extends RuntimeException {

  public abstract HttpStatus getStatus();

  public abstract String getErrorCode();
}

