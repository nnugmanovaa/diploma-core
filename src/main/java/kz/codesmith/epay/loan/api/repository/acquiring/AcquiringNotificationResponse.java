package kz.codesmith.epay.loan.api.repository.acquiring;

import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AcquiringNotificationResponse extends AcquiringBaseResponse {

  private Object data;
}
