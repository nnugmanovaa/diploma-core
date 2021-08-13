package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResultEspDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String singleAggregatePaymentResponse;
  private String errorCode;
  private String errorMessage;

}
