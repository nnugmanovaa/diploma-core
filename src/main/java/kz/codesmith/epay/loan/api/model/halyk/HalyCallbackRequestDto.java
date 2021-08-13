package kz.codesmith.epay.loan.api.model.halyk;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
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
public class HalyCallbackRequestDto {

  @XmlElement(name = "bank")
  private Bank bank;

  @XmlElement(name = "bank_sign")
  private BankSign bankSign;

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "bank")
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class Bank implements Serializable {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "customer")
    private Customer customer;

    @XmlElement(name = "results")
    private PaymentResults paymentResults;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "customer")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Customer implements Serializable {

      @XmlAttribute(name = "name")
      private String name;

      @XmlAttribute(name = "mail")
      private String email;

      @XmlAttribute(name = "phone")
      private String msisdn;

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

        @XmlAttribute(name = "cert_id")
        private String certId;

        @XmlAttribute(name = "name")
        private String name;

        @XmlAttribute(name = "MerchantID")
        private String merchantId;

        @XmlAttribute(name = "merchant_main")
        private String merchantMain;

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
          private BigDecimal amount;

          @XmlAttribute(name = "amount_with_fee")
          private BigDecimal totalAmount;

          @XmlAttribute(name = "fee")
          private BigDecimal fee;

          @XmlAttribute(name = "wofee")
          private BigDecimal woFee;

          @XmlAttribute(name = "currency")
          private int currency;

          //dd.MM.yy HH.mm.ss
          @XmlAttribute(name = "ordertime")
          private String orderTime;

        }

      }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlRootElement(name = "results")
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class PaymentResults implements Serializable {

      @XmlAttribute(name = "timestamp")
      private String timestamp;

      @XmlElement(name = "payment")
      private Payment payment;

      @XmlAccessorType(XmlAccessType.FIELD)
      @XmlRootElement(name = "payment")
      @AllArgsConstructor
      @NoArgsConstructor
      @Data
      public static class Payment implements Serializable {

        @XmlAttribute(name = "merchant_id")
        private String merchantId;

        @XmlAttribute(name = "card")
        private String card;

        @XmlAttribute(name = "card_to")
        private String cardTo;

        @XmlAttribute(name = "amount")
        private BigDecimal amount;

        @XmlAttribute(name = "reference")
        private String reference;

        @XmlAttribute(name = "approval_code")
        private String approvalCode;

        @XmlAttribute(name = "response_code")
        private String result;

        @XmlAttribute(name = "Secure")
        private String secure;

        @XmlAttribute(name = "card_bin")
        private String cardBin;

      }
    }
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlRootElement(name = "bank_sign")
  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class BankSign implements Serializable {

    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "cert_id")
    private String certId;

    @XmlValue
    private String content;
  }
}
