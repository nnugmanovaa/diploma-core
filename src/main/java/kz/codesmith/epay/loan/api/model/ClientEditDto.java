package kz.codesmith.epay.loan.api.model;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ClientEditDto {
  private String firstName;
  private String lastName;
  private String iin;
  private LocalDate birthDate;
  private String email;
  private String clientName;
}
