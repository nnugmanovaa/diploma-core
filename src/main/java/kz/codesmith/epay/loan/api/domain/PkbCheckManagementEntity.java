package kz.codesmith.epay.loan.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pkb_checks_management", schema = "loan")
@Data
public class PkbCheckManagementEntity {

  @Id
  @Column(name = "code")
  private String code;

  @Column(name = "title")
  private String title;

  @Column(name = "is_active")
  private Boolean isActive;

  @Column(name = "rejection_text")
  private String rejectionText;

}
