package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationReport implements Serializable {

  private String requestId;
  private String dateApplication;
  private String iin;
  private Double kdnScore;
  private Double debt;
  private Double income;
  private IncomesResultCrtrV2 incomesResultCrtrV2;
  private String errorCode;
  private String errorMessage;
}
