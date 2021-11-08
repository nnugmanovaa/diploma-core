package kz.codesmith.epay.loan.api.model.scoring;

import org.apache.commons.lang3.StringUtils;

public enum RejectionReason implements Reason {
  BAD_SCORE_OR_RATE("score or badRate did not pass the check"),
  BAD_EFFECTIVE_RATE("effective rate core did not pass the check"),
  NO_ABILITY_TO_PAY("income is less than cost of living"),
  NO_PREVIOUS_HIST_AND_DEDUCTIONS("no previous credit history and deductions found"),
  KDN_INCOME_OR_DEBT_UNAVAILABLE("could not calculate new kdn"),
  KDN_TOO_BIG("KDN score is bigger than expected"),
  PREVIOUS_OVERDUES("There are overdue payments in last months"),
  PREVIOUS_OVERDUES_CHECK_FAILED("Can't check overdues due to missing report for subj"),
  SCORING_ERRORS("Errors occurred during scoring process. Check logs.");

  private final String description;

  RejectionReason(String description) {
    this.description = description;
  }


  @Override
  public String description() {
    return this.description;
  }

  @Override
  public String fullName() {
    return StringUtils.join(name(), "(", description(), ")");
  }
}
