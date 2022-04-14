package kz.codesmith.epay.loan.api.diploma.model;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimplePasswordChangeDto {

  @NotBlank
  private String password;

  @NotBlank
  private String clientName;
}
