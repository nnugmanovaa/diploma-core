package kz.codesmith.epay.loan.api.model.halyk;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "document")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class HalykCardP2PTransferDocument implements Serializable {

  @XmlElement(name = "merchant")
  private Merchant merchant;

  @XmlElement(name = "merchant_sign")
  private MerchantSign merchantSign;

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "merchant")
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class Merchant implements Serializable {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "merchant_id")
    private String merchantId;

    @XmlAttribute(name = "merchant_main")
    private String merchantMain;

    @XmlAttribute(name = "cert_id")
    private String certId;

    @XmlElement(name = "order")
    private Order order;

    @XmlAccessorType(XmlAccessType.FIELD)
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Data
    public static class Order implements Serializable {

      @XmlAttribute(name = "card_id_from")
      private String cardIdFrom;

      @XmlAttribute(name = "order_id")
      private Integer orderId;

      @XmlAttribute(name = "amount")
      private Integer amount;

      @XmlAttribute(name = "currency")
      private int currency;

      //dd.MM.yy HH.mm.ss
      @XmlAttribute(name = "ordertime")
      private String orderTime;

    }

  }
}
