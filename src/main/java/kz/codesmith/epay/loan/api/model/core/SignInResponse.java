package kz.codesmith.epay.loan.api.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignInResponse {

  private String accessToken;
  private Long id;
  private List<String> roles;
  private String type;
  private String username;

}
