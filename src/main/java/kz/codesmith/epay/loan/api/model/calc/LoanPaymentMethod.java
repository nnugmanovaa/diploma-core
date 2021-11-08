package kz.codesmith.epay.loan.api.model.calc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanPaymentMethod implements Serializable {

  private String methodId;
  private String methodNameRu;
  private String methodNameKk;
  private boolean isDefault;
}
