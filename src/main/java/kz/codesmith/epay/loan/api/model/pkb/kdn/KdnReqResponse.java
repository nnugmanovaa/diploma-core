package kz.codesmith.epay.loan.api.model.pkb.kdn;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KdnReqResponse implements Serializable {

  private Return theReturn;
}
