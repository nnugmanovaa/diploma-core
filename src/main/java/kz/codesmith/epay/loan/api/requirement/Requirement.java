package kz.codesmith.epay.loan.api.requirement;

public interface Requirement<T extends RequirementContext> {

  RequirementResult check(T context);

  default RequirementResult check() {
    return check(null);
  }

}
