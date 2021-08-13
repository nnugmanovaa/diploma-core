package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportCrtrV2Dto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String fcbStatus;
  private String fcbMessage;
  private SyncSendMessageResponseDto result;
  private ResultAspDto resultASP;
  private ResultEspDto resultESP;

}
