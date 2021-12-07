package kz.codesmith.epay.loan.api.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "blocked_users")
public class BlockedUserEntity {
  @Id
  @Column(name = "id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "seq_blocked_users")
  @SequenceGenerator(
      sequenceName = "blocked_users_seq_id",
      name = "seq_blocked_users",
      allocationSize = 1, schema = "loan")
  private Long id;

  @Column(name = "iin")
  private String iin;

  @Column(name = "blocked_reason")
  private String blockedReason;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "author")
  private String author;

  @PrePersist
  public void toCreate() {
    setCreatedAt(LocalDateTime.now());
  }
}
