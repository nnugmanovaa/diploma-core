package kz.codesmith.epay.loan.api.model.pkb;

import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import kz.codesmith.epay.loan.api.util.LocalDateTimeAdapter;
import lombok.Data;

@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class TitleValueAsDate {

  @XmlAttribute(name = "title")
  private String title;

  @XmlAttribute(name = "value")
  @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
  private LocalDateTime value;

}
