package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SyncMessageInfoResponseDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String messageId;
  private String correlationId;
  private LocalDateTime responseDate;
  private StatusInfoDto status;
  private String sessionId;

}
