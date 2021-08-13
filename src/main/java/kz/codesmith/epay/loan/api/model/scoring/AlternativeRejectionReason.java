package kz.codesmith.epay.loan.api.model.scoring;

import org.apache.commons.lang3.StringUtils;

public enum AlternativeRejectionReason implements Reason {
  NEW_KDN_TOO_BIG("new kdn is bigger than expected“");

  private final String description;

  AlternativeRejectionReason(String description) {
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
