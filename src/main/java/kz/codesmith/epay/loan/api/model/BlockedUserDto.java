package kz.codesmith.epay.loan.api.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BlockedUserDto {
  private Long id;
  private String iin;
  private String blockedReason;
  private LocalDateTime createdAt;
  private String author;
}
