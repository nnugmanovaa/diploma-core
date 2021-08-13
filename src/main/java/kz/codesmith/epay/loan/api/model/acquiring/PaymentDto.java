package kz.codesmith.epay.loan.api.model.acquiring;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

  @NotNull
  @Positive
  private Double amount;

  private String extClientRef;

  private String cardsId;

  @Email
  private String email;

  @NotBlank
  private String currency;

  @NotBlank
  private String extOrdersId;

  private ZonedDateTime extOrdersTime;

  @NotBlank
  private String description;

  @URL
  @NotBlank
  private String successReturnUrl;

  @URL
  @NotBlank
  private String errorReturnUrl;

  private boolean shortenPaymentUrl = false;

  private Map<String, Object> payload = new HashMap<>();

}