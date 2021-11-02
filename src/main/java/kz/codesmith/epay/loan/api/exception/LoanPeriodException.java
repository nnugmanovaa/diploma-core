package kz.codesmith.epay.loan.api.exception;

import javax.validation.ValidationException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LoanPeriodException extends ValidationException {

  private String message;
  private Integer minPeriod;
  private Integer maxPeriod;

  public LoanPeriodException(String message, Integer minPeriod, Integer maxPeriod) {
    super(message);
    this.message = message;
    this.minPeriod = minPeriod;
    this.maxPeriod = maxPeriod;
  }

}
