package kz.codesmith.epay.loan.api.requirement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import kz.codesmith.epay.loan.api.model.scoring.CheckMode;

public class CompositeRequirement<T extends RequirementContext> implements Requirement<T> {

  private final CheckMode mode;
  private final RequirementFactory factory;
  private final List<Class> requirementClasses;
  private final Predicate<T> additionalFailFastPredicate;

  private CompositeRequirement(
      CheckMode mode,
      RequirementFactory factory,
      List<Class> requirementClasses,
      Predicate<T> additionalFailFastPredicate
  ) {
    this.mode = mode;
    this.factory = factory;
    this.requirementClasses = requirementClasses;
    this.additionalFailFastPredicate = additionalFailFastPredicate;
  }

  @Override
  public RequirementResult check(T context) {
    var errors = new ArrayList<String>();

    for (var requirementClass : requirementClasses) {
      @SuppressWarnings("unchecked")
      var requirement = factory.get(requirementClass);

      @SuppressWarnings("unchecked")
      var result = requirement.check(context);

      errors.addAll(result.getErrors());

      if (mode == CheckMode.FailFast && result.isNotSuccessful()
          && additionalFailFastPredicate.test(context)) {
        break;
      }
    }

    return RequirementResult.from(errors);
  }

  public static class Builder<T extends RequirementContext> {

    private CheckMode mode;
    private RequirementFactory factory;
    private final List<Class> requirements = new ArrayList<>();
    private Predicate<T> additionalFailFastPredicate;

    public Builder<T> mode(CheckMode mode) {
      this.mode = mode;
      return this;
    }

    public Builder<T> additionalFailFastCondition(Predicate<T> predicate) {
      this.additionalFailFastPredicate = predicate;
      return this;
    }

    public Builder<T> factory(RequirementFactory factory) {
      this.factory = factory;
      return this;
    }

    public <R extends Requirement> Builder<T> requirement(Class<R> clazz) {
      requirements.add(clazz);
      return this;
    }

    public CompositeRequirement<T> build() {
      if (mode == null) {
        mode = CheckMode.Continue;
      }

      if (factory == null) {
        factory = new SimpleRequirementFactory();
      }

      if (additionalFailFastPredicate == null) {
        additionalFailFastPredicate = o -> true;
      }

      return new CompositeRequirement<>(mode, factory, requirements, additionalFailFastPredicate);
    }
  }
}
