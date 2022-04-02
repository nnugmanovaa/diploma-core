package kz.codesmith.epay.loan.api.diploma.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.springboot.validators.iin.Iin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoringRequest {

  @Iin
  private String iin;

  @NotNull
  private PersonalInfoDto personalInfo;

  @NotNull
  @Positive
  private Double loanAmount;

  @NotNull
  @Positive
  private Integer loanPeriod;
}
