package kz.codesmith.epay.loan.api.repository.acquiring;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AcquiringBaseResponse {
  private String message;

  private AcquiringBaseStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")
  private LocalDateTime operationTime;
}
