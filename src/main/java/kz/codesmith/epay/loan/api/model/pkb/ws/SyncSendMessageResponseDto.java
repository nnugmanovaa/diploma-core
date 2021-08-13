package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SyncSendMessageResponseDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private SyncMessageInfoResponseDto responseInfo;
  private ResponseDataDto responseData;

}
