package kz.codesmith.epay.loan.api.model.orders;

import javax.validation.constraints.NotBlank;
import kz.codesmith.epay.core.shared.model.accounts.StrongPassword;
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
public class PasswordChangeDto {

  @NotBlank
  @StrongPassword
  private String password;

  @NotBlank
  private String clientName;
}
