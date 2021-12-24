package kz.codesmith.epay.loap.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.ws.rs.core.Response;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.loan.api.component.SpringContext;
import kz.codesmith.epay.loan.api.component.acquiring.AcquiringRs;
import kz.codesmith.epay.loan.api.configuration.payout.PayoutProperties;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringOrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.payout.PaymentType;
import kz.codesmith.epay.loan.api.model.payout.PayoutRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutResponseDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardRequestDto;
import kz.codesmith.epay.loan.api.model.payout.PayoutToCardResponseDto;
import kz.codesmith.epay.loan.api.repository.PaymentRepository;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.epay.loan.api.service.impl.PayoutServiceImpl;
import kz.codesmith.epay.security.model.UserContext;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

@PowerMockIgnore({"javax.xml.*", "org.xml.*"})
@PrepareForTest(value = {SpringContext.class})
@RequiredArgsConstructor
public class PayoutImplTest extends PowerMockTestCase {
  @Mock
  AcquiringRs acquiringRs;

  @Mock
  PayoutProperties payoutProperties;

  @Mock
  UserContextHolder userContextHolder;

  @Mock
  ILoanOrdersService loanOrdersService;

  @Mock
  PaymentRepository paymentRepository;

  @Mock
  ObjectMapper objectMapper;

  @Mock
  IMessageService messageService;

  @InjectMocks
  PayoutServiceImpl payoutService;

  public void initMocks() {
    PowerMockito.mockStatic(SpringContext.class);

    PowerMockito
        .when(SpringContext.getBean(PayoutProperties.class))
        .thenReturn(payoutProperties);
    PowerMockito
        .when(SpringContext.getBean(UserContextHolder.class))
        .thenReturn(userContextHolder);
    PowerMockito
        .when(SpringContext.getBean(AcquiringRs.class))
        .thenReturn(acquiringRs);
    PowerMockito
        .when(payoutProperties.getEnabled())
        .thenReturn(true);
    PowerMockito
        .when(payoutProperties.getCardIdFrom())
        .thenReturn("card_cxvnDYLfTjqDSdCRR7alo8-YGJdJzIby");
    PowerMockito
        .when(SpringContext.getBean(ILoanOrdersService.class))
        .thenReturn(loanOrdersService);
    PowerMockito
        .when(userContextHolder.getContext())
        .thenReturn(UserContext.builder()
            .operatorsId(1000)
            .ownerId(1002)
            .ownerType(OwnerType.CLIENT)
            .username("77051366182")
        .build());

    PowerMockito.when(SpringContext.getBean(ObjectMapper.class)).thenReturn(objectMapper);
    PowerMockito.when(SpringContext.getBean(PaymentRepository.class)).thenReturn(paymentRepository);
    PowerMockito.when(SpringContext.getBean(IMessageService.class)).thenReturn(messageService);
  }


  @Test
  public void testInitPayout() throws JsonProcessingException {
    initMocks();
    String response = "{\"merchantName\":\"TOO PITECH\",\"extOrdersId\":\"payout-order-id-2\","
        + "\"amount\":100,\"uuid\":\"096d73ac-f188-4304-a4a8-f08198e76677\","
        + "\"orderDate\":{\"year\":2021,\"month\":\"OCTOBER\",\"monthValue\":10,\"dayOfMonth\":6,"
        + "\"dayOfWeek\":\"WEDNESDAY\",\"dayOfYear\":279,\"era\":\"CE\",\"leapYear\":false,"
        + "\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"}},"
        + "\"ordersId\":\"122614138927421\",\"currency\":\"KZT\",\"state\":\"NEW\","
        + "\"paymentUrl\":\"http://localhost:8081/pay/order?ordersId=122614138927421"
        + "&orderDate=2021-10-06&uuid=096d73ac-f188-4304-a4a8-f08198e76677\","
        + "\"successReturnUrl\":\"http://success-page\","
        + "\"errorReturnUrl\":\"http://error-page\","
        + "\"cardsId\":\"card_cxvnDYLfTjqDSdCRR7alo8-YGJdJzIby\",\"payload\":null,"
        + "\"description\":\"Описание выплаты\",\"paymentType\":\"PAYOUT_CARD\"}";

    Response httpResponse = Response.ok(response)
        .status(200)
        .build();

    OrderDto orderDto = OrderDto.builder()
        .orderId(100028)
        .status(OrderState.CASH_OUT_CARD_INITIALIZED)
        .loanAmount(BigDecimal.valueOf(500))
        .orderExtRefId("1-000000081")
        .contractExtRefTime(LocalDateTime.now())
        .build();

    PowerMockito.when(acquiringRs.payout(getPayoutRequest())).thenReturn(httpResponse);


    PayoutToCardRequestDto requestDto = PayoutToCardRequestDto.builder()
        .backFailureLink("https://google.com")
        .backSuccessLink("https://google.com")
        .toCardsId("123456")
        .orderId(100028)
        .build();

    PowerMockito
        .when(loanOrdersService.getOrderByUserOwner(requestDto.getOrderId()))
        .thenReturn(orderDto);

    PayoutResponseDto payoutResponseDto = PayoutResponseDto.builder()
        .state(AcquiringOrderState.SUCCESS)
        .uuid("096d73ac-f188-4304-a4a8-f08198e76677")
        .paymentUrl("http://localhost:8081/pay/order?ordersId=122614138927421"
            + "&orderDate=2021-10-06&uuid=096d73ac-f188-4304-a4a8-f08198e76677")
        .ordersId("122614138927421")
        .orderDate(LocalDate.of(2021, 10, 6))
        .paymentType(PaymentType.PAYOUT_CARD)
        .merchantName("TOO PITECH")
        .extOrdersId("payout-order-id-2")
        .successReturnUrl("http://success-page")
        .errorReturnUrl("http://error-page")
        .description("Описание выплаты")
        .currency("KZT")
        .cardsId("1111111")
        .amount(500d)
        .build();

    PowerMockito
        .when(objectMapper.readValue(anyString(), eq(PayoutResponseDto.class)))
        .thenReturn(payoutResponseDto);
    PayoutToCardResponseDto cardResponseDto = payoutService.initPayoutToCard(requestDto);

    Assert.assertNotNull(cardResponseDto);
    Assert.assertEquals(cardResponseDto.getOrderState(), AcquiringOrderState.SUCCESS);
    Assert.assertEquals(cardResponseDto.getAmount(), BigDecimal.valueOf(500));
  }

  private PayoutRequestDto getPayoutRequest() {
    return PayoutRequestDto.builder()
        .extClientRef("77051366182")
        .errorReturnUrl("https://google.com")
        .successReturnUrl("https://google.com")
        .extOrdersId("100028")
        .description("Выплата на карту")
        .currency("KZT")
        .toCardsId("123456")
        .cardsId("card_cxvnDYLfTjqDSdCRR7alo8-YGJdJzIby")
        .amount(500d)
        .build();
  }
}
