package kz.codesmith.epay.loan.api.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IncomeInfoCatalogDto {

  private Integer id;

  @NotNull
  private IncomeInfoType type;

  @NotNull
  private String value;

  private String description;

}
