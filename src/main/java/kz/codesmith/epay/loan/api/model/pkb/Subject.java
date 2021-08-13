package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;


@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Subject {

  @XmlElement(name = "Name")
  private TitleValue fullName;

  @XmlElement(name = "BirthDate")
  private TitleValue birthDate;

  @XmlElement(name = "Document")
  private TitleValue document;

}
