package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Return implements Serializable {

  private ApplicationReport applicationReport;
  private String errorCode;
  private String errorMessage;
  private String reportDate;
  private String id;
}
