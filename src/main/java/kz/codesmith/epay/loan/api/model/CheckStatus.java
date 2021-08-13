package kz.codesmith.epay.loan.api.model;

import lombok.Getter;

public enum CheckStatus {
  FOUND("1"),
  NOT_FOUND("2"),
  INACCESSIBLE("3");

  @Getter
  private String status;

  CheckStatus(String status) {
    this.status = status;
  }
}
