package kz.codesmith.epay.loan.api.repository.acquiring;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AcquiringNotificationRequest {
  @NotBlank
  private String body;
}
