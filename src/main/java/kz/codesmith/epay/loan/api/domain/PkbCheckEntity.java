package kz.codesmith.epay.loan.api.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "pkb_checks_result_history", schema = "loan")
@Data
public class PkbCheckEntity {

  @Id
  @Column(name = "pkb_checks_result_history_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "seq_pkb_checks_result_history")
  @SequenceGenerator(
      sequenceName = "pkb_checks_result_history_seq_id",
      name = "seq_pkb_checks_result_history",
      allocationSize = 1, schema = "loan")
  private Integer id;

  @Column(name = "iin")
  private String iin;

  @Column(name = "request_date")
  private LocalDateTime requestDate;

  @Column(name = "code")
  private String code;

  @Column(name = "title")
  private String title;

  @Column(name = "status")
  private String status;

  @Column(name = "source")
  private String source;

  @Column(name = "refresh_date")
  private LocalDateTime refreshDate;

  @Column(name = "actual_date")
  private LocalDateTime actualDate;

  @Column(name = "search_by")
  private String searchBy;

}
