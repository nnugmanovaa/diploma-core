package kz.codesmith.epay.loan.api.model.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.core.shared.model.services.ServiceField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Builder
public class CorePaymentReturnDto {

  @NotBlank
  private String clientName;

  @NotNull
  @Negative
  private Double billAmount;

  @NotNull
  private Integer servicesId;

  @NotBlank
  private List<ServiceField> fields;

  @NotBlank
  private String extRefOrderIdValue;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")
  private Date extRefOrderTimeValue;
}
