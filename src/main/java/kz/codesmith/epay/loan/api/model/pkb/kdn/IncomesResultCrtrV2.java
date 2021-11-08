package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomesResultCrtrV2 implements Serializable {

  private String fcbStatus;
  private String fcbMessage;
  private IncomesResult result;
}
