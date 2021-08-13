package kz.codesmith.epay.loan.api.model.acquiring;

import java.io.Serializable;
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
public class AcquiringPaymentResponse extends AcquiringBaseResponse
    implements Serializable {

  private String url;
}
