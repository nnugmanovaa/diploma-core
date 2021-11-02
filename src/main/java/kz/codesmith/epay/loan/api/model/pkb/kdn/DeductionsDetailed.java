package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeductionsDetailed implements Serializable {

  private String bin;
  private String date;
  private String name;
  private Double amount;
}
