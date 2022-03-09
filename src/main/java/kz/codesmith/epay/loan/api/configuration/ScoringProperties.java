package kz.codesmith.epay.loan.api.configuration;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@ConfigurationProperties("scoring")
public class ScoringProperties {
  private double maxKdn;
  private double maxDecil;
  private boolean enabled;
  private float interestRate;
  private String ownScoreUrl;
  private boolean checkOpenLoans;
  private float pdlMaxInterestRate;
}
