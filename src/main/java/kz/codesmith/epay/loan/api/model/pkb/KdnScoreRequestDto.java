package kz.codesmith.epay.loan.api.model.pkb;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.springboot.validators.iin.Iin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class KdnScoreRequestDto {

  @Iin
  private String iin;

  @NotNull
  private LocalDate birthDate;

  @NotBlank
  private String firstName;

  @NotBlank
  private String lastName;

  private String middleName;
}
