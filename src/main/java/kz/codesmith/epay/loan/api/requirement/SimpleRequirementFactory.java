package kz.codesmith.epay.loan.api.requirement;

public class SimpleRequirementFactory implements RequirementFactory {

  @Override
  public <T extends Requirement> T get(Class<T> type) {
    try {
      return type.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("%s must have ctor without params.", type.toString()), e);
    }
  }

}
