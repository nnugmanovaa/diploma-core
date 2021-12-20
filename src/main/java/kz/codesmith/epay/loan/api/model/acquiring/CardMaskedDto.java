package kz.codesmith.epay.loan.api.model.acquiring;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardMaskedDto implements Serializable {

  private String mask;
  private String owner;
  private String issuer;
}
