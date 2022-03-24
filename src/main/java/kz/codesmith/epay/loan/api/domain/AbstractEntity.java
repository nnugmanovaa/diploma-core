package kz.codesmith.epay.loan.api.domain;

import kz.codesmith.epay.core.shared.converter.Utils;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AbstractEntity implements Serializable {

  @Column(name = "inserted_time", updatable = false)
  LocalDateTime insertedTime;

  @Column(name = "updated_time")
  LocalDateTime updatedTime;

  @Column(name = "updated_by")
  String updatedBy;

  @Column(name = "inserted_by", updatable = false)
  String insertedBy;

  @PrePersist
  public void toCreate() {
    setInsertedTime(Utils.now());
    setUpdatedTime(getInsertedTime());
  }

  @PreUpdate
  public void toUpdate() {
    setUpdatedTime(Utils.now());
  }

}
