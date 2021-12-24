package kz.codesmith.epay.loan.api.model.acquiring;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcquiringCardSaveDto {
  @NotBlank
  private String extClientRef;

  @NotBlank
  @URL
  private String successReturnUrl;

  @NotBlank
  @URL
  private String errorReturnUrl;

  @Positive
  private Double amount;

  private String currency;

  private String extOrdersId;

  @Builder.Default
  private boolean shortenPaymentUrl = true;

  private String template;
}
