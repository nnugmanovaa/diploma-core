package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TitleValue {

  @XmlAttribute(name = "title")
  private String title;

  @XmlAttribute(name = "value")
  private String value;

}
