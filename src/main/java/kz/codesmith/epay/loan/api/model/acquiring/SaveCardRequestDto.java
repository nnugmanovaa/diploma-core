package kz.codesmith.epay.loan.api.model.acquiring;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveCardRequestDto {
  private String client;

  @NotBlank
  @URL
  private String successReturnUrl;

  @NotBlank
  @URL
  private String errorReturnUrl;

  @Positive
  private Double amount;

  private String template;
}
