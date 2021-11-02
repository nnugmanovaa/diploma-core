package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
public class Data implements Serializable {

  private String responseCode;
  private String requestNumber;
  private String requestIin;
  private String responseDate;
  private String responseNumber;
  private List<DeductionsDetailed> deductionsDetailed;
}
