package kz.codesmith.epay.loan.api.model.cashout;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@Builder
public class InitClientWalletTopUpDirectDto {

  @NotBlank
  private String iin;

  @NotNull
  private Double amount;

  @NotBlank
  private String description;

  @NotBlank
  private String operation;

  @NotBlank
  private String extRefOrderIdValue;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSZ")
  private Date extRefOrderTimeValue;
}
