package kz.codesmith.epay.loan.api.requirement;

public interface RequirementFactory {
  <T extends Requirement> T get(Class<T> type);
}
