package kz.codesmith.epay.loan.api.model;

import java.util.List;
import lombok.Getter;

public class CheckResult<T> {

  @Getter
  private final List<T> errors;

  public CheckResult(List<T> errors) {
    this.errors = errors;
  }

}
