package kz.codesmith.epay.loan.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientExistDto {
  private String clientName;
  private Boolean exist;
}
