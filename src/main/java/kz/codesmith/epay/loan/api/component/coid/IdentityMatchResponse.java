package kz.codesmith.epay.loan.api.component.coid;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityMatchResponse {

  private String iin;
  private Double result;
  private String vendor;
  @JsonProperty("x_idempotency_key")
  private String idempotencyKey;
}
