package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import kz.codesmith.epay.loan.api.model.pkb.TitleValue;
import lombok.Data;
import lombok.EqualsAndHashCode;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
@EqualsAndHashCode(callSuper = true)
public class IdTitleValue extends TitleValue {

  @XmlAttribute(name = "id")
  private String id;

}
