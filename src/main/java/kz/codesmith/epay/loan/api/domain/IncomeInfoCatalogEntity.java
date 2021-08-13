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
import kz.codesmith.epay.loan.api.model.IncomeInfoType;
import lombok.Data;

@Entity
@Table(name = "income_info_catalog", schema = "loan")
@Data
public class IncomeInfoCatalogEntity {

  @Id
  @Column(name = "income_info_catalog_id")
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "seq_income_info_catalog_seq")
  @SequenceGenerator(
      sequenceName = "income_info_catalog_seq_id",
      name = "seq_income_info_catalog_seq",
      allocationSize = 1, schema = "loan")
  private Integer id;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private IncomeInfoType type;

  @Column(name = "value")
  private String value;

  @Column(name = "description")
  private String description;

}
