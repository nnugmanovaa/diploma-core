package kz.codesmith.epay.loan.api.requirement;

import java.util.List;

public class RequirementResult<T> {

  private final List<T> errors;

  protected RequirementResult(List<T> errors) {
    this.errors = errors;
  }

  public List<T> getErrors() {
    return errors;
  }

  public boolean isSuccessful() {
    return errors.isEmpty();
  }

  public boolean isNotSuccessful() {
    return !isSuccessful();
  }

  public static <E> RequirementResult<E> from(List<E> errors) {
    return new RequirementResult<E>(List.copyOf(errors));
  }

  public static <E> RequirementResult success() {
    return new RequirementResult<E>(List.of());
  }

  public static <E> RequirementResult failure(E error) {
    return new RequirementResult<E>(List.of(error));
  }
}
