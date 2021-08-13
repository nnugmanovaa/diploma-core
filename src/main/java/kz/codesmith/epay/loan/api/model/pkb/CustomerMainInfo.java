package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class CustomerMainInfo {

  @XmlAttribute(name = "title")
  private String title;

  @XmlElement(name = "DataIssue")
  private TitleValueAsDate requestDate;

  @XmlElement(name = "IinBin")
  private TitleValue iinBin;

  @XmlElement(name = "Subject")
  private Subject subject;

}
