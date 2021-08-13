package kz.codesmith.epay.loan.api.model.halyk;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "merchant_sign")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantSign implements Serializable {

  private String type;

  private String content;

  @XmlValue
  public String getContent() {
    return "".equals(content) ? null : content;
  }

  public void setContent(String content) {
    this.content = "".equals(content) ? null : content;
  }

  @XmlAttribute(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
