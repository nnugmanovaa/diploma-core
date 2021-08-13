package kz.codesmith.epay.loan.api.model.pkb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Check {

  @XmlAttribute(name = "code")
  private String code;

  @XmlAttribute(name = "title")
  private String title;

  @XmlElement(name = "Source")
  private TitleValue source;

  @XmlElement(name = "RefreshDate")
  private TitleValueAsDate refreshDate;

  @XmlElement(name = "ActualDate")
  private TitleValueAsDate actualDate;

  @XmlElement(name = "Status")
  private IdTitleValue status;

  @XmlElement(name = "SearchBy")
  private IdTitleValue searchBy;

}
