package kz.codesmith.epay.loan.api.model.scoring;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.andreinc.jbvext.annotations.str.Numeric;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DocumentDto {

  @NotBlank
  @Numeric
  private String idNumber;

  private String nationality;

  @NotBlank
  private String issuedBy;

  @NotBlank
  private LocalDate issuedDate;

  @NotNull
  private LocalDate expireDate;

}
