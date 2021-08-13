package kz.codesmith.epay.loan.api.model.pkb;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import kz.codesmith.epay.loan.api.util.LocalDateTimeAdapter;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DynamicCheck {

  @XmlElement(name = "status")
  private Status status;

  @XmlElement(name = "date")
  @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
  private LocalDateTime date;

  @XmlElement(name = "title")
  private String title;

  @XmlElement(name = "code")
  private String code;

  @XmlElement(name = "url")
  private String url;

  @XmlElement(name = "type")
  private String type;

}
