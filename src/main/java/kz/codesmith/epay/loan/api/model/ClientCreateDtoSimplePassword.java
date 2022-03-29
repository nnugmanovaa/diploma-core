package kz.codesmith.epay.loan.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(Include.NON_NULL)
public class ClientCreateDtoSimplePassword extends ClientDto {
  private String password;

  private Integer agentsId;

  private boolean sendOtp = true;
}
