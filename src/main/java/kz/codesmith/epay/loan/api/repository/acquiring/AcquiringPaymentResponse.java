package kz.codesmith.epay.loan.api.repository.acquiring;

import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcquiringPaymentResponse {
  private String url;
  private String message;

  private AcquiringBaseStatus status;

  private LocalDateTime operationTime;
}
