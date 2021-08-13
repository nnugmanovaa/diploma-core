package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Status {

  @XmlElement(name = "text")
  private String text;

  @XmlElement(name = "id")
  private String id;

}
