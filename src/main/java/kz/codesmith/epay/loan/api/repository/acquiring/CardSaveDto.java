package kz.codesmith.epay.loan.api.repository.acquiring;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import kz.codesmith.epay.loan.api.model.acquiring.SimpleState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class CardSaveDto {

  private String cardsId;

  @NotBlank
  private String maskedPan;

  @NotBlank
  private String holderName;

  @NotBlank
  private String issuer;

  @NotNull
  private Integer clientsId;

  private SimpleState state;

  private LocalDateTime createTime;

  private LocalDateTime updateTime;

  private boolean isConfirmed;

}
