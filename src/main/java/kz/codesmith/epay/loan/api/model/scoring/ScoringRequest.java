package kz.codesmith.epay.loan.api.model.scoring;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import kz.codesmith.springboot.validators.iin.Iin;
import kz.payintech.ListLoanMethod;
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

  private String loanProduct;

  @NotNull
  private ListLoanMethod loanMethod;

  @JsonIgnore
  private boolean isWhiteList = false;

  private String preScoreRequestId;

  @JsonIgnore
  private boolean isBlackList = false;
}
