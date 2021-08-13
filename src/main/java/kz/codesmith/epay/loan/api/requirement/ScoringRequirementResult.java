package kz.codesmith.epay.loan.api.requirement;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import kz.codesmith.epay.loan.api.model.scoring.AlternativeRejectionReason;
import kz.codesmith.epay.loan.api.model.scoring.Reason;
import kz.codesmith.epay.loan.api.model.scoring.ScoringResult;

public class ScoringRequirementResult<T extends Reason> extends RequirementResult<T> {
  private final RequirementResult<T> delegate;

  ScoringRequirementResult(RequirementResult<T> delegate) {
    super(delegate.getErrors());
    this.delegate = delegate;
  }

  @Override
  public List<T> getErrors() {
    return delegate.getErrors();
  }

  @Override
  public boolean isSuccessful() {
    return delegate.isSuccessful();
  }

  @Override
  public boolean isNotSuccessful() {
    return delegate.isNotSuccessful();
  }

  public ScoringResult getResult() {
    if (getErrors().isEmpty()) {
      return ScoringResult.APPROVED;
    }

    if (isErrorsAlternative()) {
      return ScoringResult.ALTERNATIVE;
    }

    return ScoringResult.REFUSED;
  }

  private boolean isErrorsAlternative() {
    return Arrays.asList(AlternativeRejectionReason.values()).containsAll(getErrors());
  }

  public String getErrorsString(String delimiter) {
    return getErrors().stream().map(Reason::fullName).collect(Collectors.joining(delimiter));
  }

}
