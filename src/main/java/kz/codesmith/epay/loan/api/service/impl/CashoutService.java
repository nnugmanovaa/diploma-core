package kz.codesmith.epay.loan.api.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.core.shared.utils.LanguageUtils;
import kz.codesmith.epay.loan.api.component.HalykBankSigner;
import kz.codesmith.epay.loan.api.configuration.halyk.HalykBankProperties;
import kz.codesmith.epay.loan.api.model.cashout.CardCashoutRequestDto;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.halyk.HalyCallbackRequestDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardCashoutResponseDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardP2PTransferDocument;
import kz.codesmith.epay.loan.api.model.halyk.MerchantSign;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.service.ICashoutService;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.ws.connector.utils.JaxBUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CashoutService implements ICashoutService {

  private final ILoanOrdersService loanOrdersService;
  private final HalykBankProperties halykBankProperties;
  private final JaxBUtils jaxBUtils;
  private final HalykBankSigner halykBankSigner;

  @Override
  public void initCashoutToWallet(Integer orderId) {
    throw new MfoGeneralApiException("Not Allowed");
  }

  @SneakyThrows
  @Override
  public HalykCardCashoutResponseDto initHalykEpayCashoutToCard(CardCashoutRequestDto request) {
    var order = loanOrdersService.getOrderByUserOwner(request.getOrderId());
    if (Objects.isNull(order)) {
      throw new NotFoundApiServerException(
          ApiErrorTypeParamValues.ORDER,
          request.getOrderId()
      );
    }
    if (OrderState.CONFIRMED.equals(order.getStatus())
        || OrderState.CASH_OUT_CARD_INITIALIZED.equals(order.getStatus())
        || OrderState.CASH_OUT_CARD_FAILED.equals(order.getStatus())) {
      var halykCardP2PTransfer = HalykCardP2PTransferDocument.builder()
          .merchant(HalykCardP2PTransferDocument.Merchant.builder()
              .certId(halykBankProperties.getCertificateId())
              .merchantId(halykBankProperties.getMerchantId())
              .merchantMain(halykBankProperties.getMerchantMainTerminalId())
              .name(halykBankProperties.getMerchantName())
              .order(HalykCardP2PTransferDocument.Merchant.Order.builder()
                  .amount(order.getLoanAmount().intValue())
                  .cardIdFrom(halykBankProperties.getCardIdFrom())
                  .currency(398)
                  .orderId(order.getOrderId())
                  .build())
              .build())
          .build();

      var merchantXml = jaxBUtils.marshall(halykCardP2PTransfer.getMerchant(), false);

      String merchantSign = Base64.encodeBase64String(halykBankSigner.sign(merchantXml));

      halykCardP2PTransfer.setMerchantSign(MerchantSign.builder()
          .content(merchantSign)
          .type("RSA")
          .build());

      var documentXml = jaxBUtils.marshall(halykCardP2PTransfer, false);

      log.info(documentXml);

      Map<String, Object> formData = new HashMap<>();
      formData.put("Signed_Order_B64", Base64.encodeBase64String(documentXml.getBytes()));
      formData.put("BackLink", request.getBackSuccessLink());
      formData.put("FailureBackLink", request.getBackFailureLink());
      formData.put("PostLink", halykBankProperties.getPostLink());
      formData.put("FailurePostLink", halykBankProperties.getPostLink());
      formData.put("Language", LanguageUtils.asHalykEpayLanguage(request.getLanguage()));
      formData.put("template", halykBankProperties.getCardTransferTemplate());

      var response = HalykCardCashoutResponseDto.builder()
          .url(halykBankProperties.getCardTransferSubmitUrl())
          .httpMethod(HttpMethod.POST.name())
          .formData(formData)
          .orderId(order.getOrderId())
          .amount(order.getLoanAmount())
          .build();

      loanOrdersService.updateLoanOrderCashoutCardInitInfo(
          order.getOrderId(),
          OrderState.CASH_OUT_CARD_INITIALIZED,
          response
      );

      return response;
    } else {
      throw new MfoGeneralApiException("Wrong status " + order.getStatus());
    }
  }

  @SneakyThrows
  @Override
  public void acceptHalykBankCallbackRequest(String requestXml) {

    var request = jaxBUtils.unmarshall(requestXml, HalyCallbackRequestDto.class);

    if (Objects.nonNull(request)) {

      var isVerified = halykBankSigner.verify(
          jaxBUtils.marshall(request.getBank(), false),
          request.getBankSign().getContent()
      );

      if (!isVerified) {
        throw new MfoGeneralApiException("Bank sign verification failed");
      }

      var isSameMerchant = halykBankProperties.getMerchantId()
          .equalsIgnoreCase(request.getBank().getCustomer().getMerchant().getMerchantId());

      if (!isSameMerchant) {
        throw new MfoGeneralApiException("Request from another merchant");
      }

      var orderId = request.getBank().getCustomer().getMerchant().getOrder().getOrderId();

      if (request.getBank().getPaymentResults().getPayment().getResult().equalsIgnoreCase("00")) {
        loanOrdersService.updateLoanOrderCashoutCardBankResponseInfo(
            orderId,
            OrderState.CASHED_OUT_CARD,
            request
        );
      } else {
        loanOrdersService.updateLoanOrderCashoutCardBankResponseInfo(
            orderId,
            OrderState.CASH_OUT_CARD_FAILED,
            request
        );
      }

    } else {
      log.warn("Empty callback request");
    }

  }
}
