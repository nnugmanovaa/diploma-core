package kz.codesmith.epay.loan.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import kz.codesmith.epay.loan.api.model.scoring.ScoringVarType;
import lombok.Data;

@Entity
@Table(name = "scoring_vars", schema = "loan")
@Data
public class ScoringVarEntity {

  @Id
  @Column(name = "scoring_vars_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "seq_scoring_vars")
  @SequenceGenerator(
      sequenceName = "scoring_vars_seq_id",
      name = "seq_scoring_vars",
      allocationSize = 1, schema = "loan")
  private Integer id;

  @Column(name = "display_name")
  private String name;

  @Column(name = "constant_name")
  private String constantName;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private ScoringVarType type;

  @Column(name = "value")
  private String value;

  @Column(name = "description")
  private String description;

}
