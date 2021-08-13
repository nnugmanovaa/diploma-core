package kz.codesmith.epay.loan.api.requirement;

import kz.codesmith.epay.loan.api.annotation.SpringRequirement;
import kz.codesmith.epay.loan.api.model.scoring.CheckMode;
import kz.codesmith.epay.loan.api.requirement.primitive.ClientAbilityToPayRequirement;
import kz.codesmith.epay.loan.api.requirement.primitive.EffectiveRateRequirement;
import kz.codesmith.epay.loan.api.requirement.primitive.KdnCalculationRequirement;
import kz.codesmith.epay.loan.api.requirement.primitive.OverdueRequirement;
import kz.codesmith.epay.loan.api.requirement.primitive.ScoreAndBadRateRequirement;

@SpringRequirement
public class ScoringRequirement implements Requirement<ScoringContext> {

  private final Requirement<ScoringContext> requirement;

  public ScoringRequirement(RequirementFactory factory) {
    this.requirement = new CompositeRequirement.Builder<ScoringContext>()
        .mode(CheckMode.FailFast)
        .requirement(ScoreAndBadRateRequirement.class)
        .requirement(EffectiveRateRequirement.class)
        .requirement(ClientAbilityToPayRequirement.class)
        .requirement(KdnCalculationRequirement.class)
        .requirement(OverdueRequirement.class)
        .factory(factory)
        .build();
  }

  @Override
  public ScoringRequirementResult check(ScoringContext context) {
    return new ScoringRequirementResult(requirement.check(context));
  }
}
