package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IncomesServiceResponseDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String iin;
  private String lastname;
  private String firstname;
  private String middlename;
  private String birthdate;
  private String requestTime;
  private BigDecimal summdb;
  private BigDecimal summdbnetto;

}
