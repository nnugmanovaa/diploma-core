package kz.codesmith.epay.loan.api.exception;

import java.math.BigDecimal;
import javax.validation.ValidationException;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Data
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class LoanAmountException extends ValidationException {

  private String message;
  private BigDecimal minAmount;
  private BigDecimal maxAmount;

  public LoanAmountException(String message, BigDecimal minAmount, BigDecimal maxAmount) {
    super(message);
    this.message = message;
    this.minAmount = minAmount;
    this.maxAmount = maxAmount;
  }


}
