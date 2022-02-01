package kz.codesmith.epay.loan.api.model.acquiring;

import java.time.LocalDateTime;
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
