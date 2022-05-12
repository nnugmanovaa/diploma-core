package kz.codesmith.epay.loan.api.repository.acquiring;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseResponse;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class AcquiringStatusResponse extends AcquiringBaseResponse {
  private AcquiringOrderState orderState;
}
