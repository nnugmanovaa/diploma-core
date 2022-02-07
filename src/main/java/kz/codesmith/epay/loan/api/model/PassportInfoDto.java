package kz.codesmith.epay.loan.api.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PassportInfoDto {
  private String firstName;
  private String lastName;
  private String patronymic;
  private LocalDate birthDate;
  private String nationalIdNumber;
  private String nationality;
  private String nationalIdIssuer;
  private LocalDate nationalIdIssueDate;
  private LocalDate nationalIdValidDate;
  private boolean ipdl;
  private String iin;
}
