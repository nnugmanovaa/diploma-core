package kz.codesmith.epay.loan.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientChangeNameDto {
  private String oldClientName;
  private String newClientName;
}
