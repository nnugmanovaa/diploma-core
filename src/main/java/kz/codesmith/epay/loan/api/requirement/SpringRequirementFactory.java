package kz.codesmith.epay.loan.api.requirement;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringRequirementFactory implements RequirementFactory {

  private final ApplicationContext context;

  public SpringRequirementFactory(ApplicationContext context) {
    this.context = context;
  }

  @Override
  public <T extends Requirement> T get(Class<T> type) {
    return context.getBean(type);
  }
}
