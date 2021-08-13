package kz.codesmith.epay.loan.api.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVarType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScoringVarDto {

  private Integer id;

  @NotBlank
  private String name;

  @NotBlank
  private String constantName;

  @NotNull
  private ScoringVarType type;

  @NotBlank
  private String value;

  private String description;

}
