package kz.codesmith.epay.loan.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.CardMaskedDto;
import kz.codesmith.epay.loan.api.model.acquiring.PaymentCallbackEventDto;
import kz.codesmith.epay.loan.api.service.impl.AcquiringService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

@ExtendWith(MockitoExtension.class)
@PowerMockIgnore({"javax.xml.*", "org.xml.*"})
@RequiredArgsConstructor
public class AcquiringServiceTest extends PowerMockTestCase {

  PaymentCallbackEventDto eventDto = new PaymentCallbackEventDto();

  @Mock
  ObjectMapper objectMapper;

  @InjectMocks
  AcquiringService acquiringService;

  @Test
  void testInitPaymentResponseNull() {
    PaymentEntity paymentEntity = new PaymentEntity();
    initCardForEventDto();
    Map<String, Object> map = createMap();

    PowerMockito.when(objectMapper.convertValue(eventDto.getCard(), Map.class)).thenReturn(map);

    acquiringService.checkInitPaymentResponse(eventDto, paymentEntity);
    Assert.assertTrue(paymentEntity.getInitPaymentResponse().containsKey("card"));
  }

  @Test
  void testInitPaymentResponseNotNull() {
    PaymentEntity paymentEntity = new PaymentEntity();
    ObjectMapper objMapper = new ObjectMapper();
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    AcquiringPaymentResponse acquiringPaymentResponse = AcquiringPaymentResponse.builder()
        .url("https://cards-stage.pitech.kz/pay/order?ordersId=590372420567955&orderDate=2021-12"
            + "-09&uuid=5e9d9b6c-f43c-4a00-be17-0ae58f4a63ff")
        .status(AcquiringBaseStatus.ERROR)
        .message("unexpected behaviour")
        .operationTime(LocalDateTime.parse("2021-12-09 16:47:41", timeFormatter))
        .build();
    paymentEntity.setInitPaymentResponse(
        objMapper.convertValue(
            acquiringPaymentResponse,
            Map.class
        ));

    initCardForEventDto();
    Map<String, Object> map = createMap();

    PowerMockito.when(objectMapper.convertValue(eventDto.getCard(), Map.class)).thenReturn(map);

    acquiringService.checkInitPaymentResponse(eventDto, paymentEntity);
    Assert.assertTrue(paymentEntity.getInitPaymentResponse().containsKey("card"));
  }

  private Map<String, Object> createMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("mask", "548318-######-0293");
    map.put("issuer", "mastercard");
    return map;
  }

  private void initCardForEventDto() {
    eventDto.setCard(CardMaskedDto.builder()
        .mask("548318-######-0293")
        .issuer("mastercard")
        .build());
  }

}