package kz.codesmith.epay.loan.api.model.acquiring;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardMaskedDto {

  private String mask;
  private String owner;
  private String issuer;
}
