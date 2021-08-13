package kz.codesmith.epay.loan.api.model;

import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PkbCheckManagementDto {

  @NotNull
  private String code;

  @NotNull
  private String title;

  private Boolean isActive;

  private String rejectionText;

}
