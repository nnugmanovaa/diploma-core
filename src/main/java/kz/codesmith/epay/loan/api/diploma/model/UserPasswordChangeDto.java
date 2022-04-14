package kz.codesmith.epay.loan.api.diploma.model;

import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.model.accounts.StrongPassword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPasswordChangeDto {

  @NotBlank
  private String oldPassword;

  @NotBlank
  @StrongPassword
  private String newPassword;

  @NotBlank
  private String username;
}
