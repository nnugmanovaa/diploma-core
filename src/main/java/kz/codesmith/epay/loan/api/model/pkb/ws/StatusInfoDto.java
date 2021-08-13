package kz.codesmith.epay.loan.api.model.pkb.ws;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatusInfoDto implements Serializable {

  private static final long serialVersionUID = 7702L;
  private String code;
  private String message;

}
