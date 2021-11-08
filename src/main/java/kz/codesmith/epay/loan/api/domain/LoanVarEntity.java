package kz.codesmith.epay.loan.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_vars", schema = "loan")
public class LoanVarEntity {

  @Id
  @Column(name = "loan_var_id")
  private String loanVarId;

  @Column(name = "value_type")
  private String valueType;

  @Column(name = "value")
  private String value;
}
