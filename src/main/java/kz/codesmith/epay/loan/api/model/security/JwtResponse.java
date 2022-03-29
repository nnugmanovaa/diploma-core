package kz.codesmith.epay.loan.api.model.security;

import java.util.List;
import kz.codesmith.epay.core.shared.model.clients.ClientIdentificationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

  private final String type = "Bearer";
  private String accessToken;
  private Long id;
  private String username;
  private List<String> roles;
  private ClientIdentificationStatus identificationStatus;
  private Integer clientId;
  private String firstName;
  private String lastName;
  private String middleName;
  private String iin;
}
