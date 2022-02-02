package kz.codesmith.epay.loan.api.payment;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import kz.codesmith.epay.core.shared.model.exceptions.LoanPaymentException;
import kz.codesmith.epay.loan.api.configuration.AmqpConfig;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.domain.payments.PaymentEntity;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringBaseStatus;
import kz.codesmith.epay.loan.api.model.acquiring.AcquiringPaymentResponse;
import kz.codesmith.epay.loan.api.model.acquiring.MfoProcessingStatus;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanCheckAccountResponse;
import kz.codesmith.epay.loan.api.payment.dto.LoanDeatilsRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanDetailsResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanInfoDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentRequestDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanPaymentResponseDto;
import kz.codesmith.epay.loan.api.payment.dto.LoanStatusDto;
import kz.codesmith.epay.loan.api.payment.dto.OrderInitDto;
import kz.codesmith.epay.loan.api.payment.services.LoanPaymentServices;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.service.IAcquiringService;
import kz.codesmith.epay.loan.api.service.IMessageService;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.pitech.mfo.Contract;
import kz.pitech.mfo.PaymentApp;
import kz.pitech.mfo.PaymentServicesPortType;
import kz.pitech.mfo.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPaymentImpl implements ILoanPayment {
  private final IPaymentService paymentService;
  private final LoanPaymentServices loanPaymentServices;
  private final PaymentServicesPortType servicesPortType;
  private final MfoAppProperties mfoAppProperties;
  private final IAcquiringService acquiringService;
  private final ModelMapper modelMapper;
  private final IMessageService messageService;
  private final LoanOrdersRepository loanOrdersRepository;
  private final Environment environment;

  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private static final String ACTIVE_PROFILE_PRODUCTION = "production";

  @Override
  public LoanCheckAccountResponse getAccountLoans(LoanCheckAccountRequestDto requestDto) {
    PaymentApp paymentApp = fillPaymentAppForCheck(requestDto.getIin());

    Response response = processRequest(paymentApp);
    List<LoanInfoDto> loanInfoDtos = new ArrayList<>();
    String message;
    if (Objects.nonNull(response) && Objects.nonNull(response.getContracts())) {
      List<Contract> contracts = response.getContracts().getContract();
      if (!CollectionUtils.isEmpty(contracts)) {
        loanInfoDtos = contracts
            .stream()
            .map(this::mapToLoanInfoDto)
            .map(this::fillLoanStatus)
            .collect(Collectors.toList());
        message = LoanPaymentConstants.LOANS_LIST;
      } else {
        message = LoanPaymentConstants.NO_ACTIVE_LOANS;
      }
    } else {
      message = LoanPaymentConstants.CANNOT_GET_LOANS;
    }

    return LoanCheckAccountResponse.builder().message(message).loans(loanInfoDtos).build();
  }

  private PaymentApp fillPaymentAppForCheck(String iin) {
    PaymentApp paymentApp = new PaymentApp();
    paymentApp.setTxnId("1");
    paymentApp.setServiceName(mfoAppProperties.getServiceName());
    paymentApp.setPayType(BigInteger.valueOf(loanPaymentServices.getPlannedRePaymentProductId()));
    paymentApp.setCommand(LoanPaymentConstants.TYPE_CHECK);
    paymentApp.setAccount(iin);
    paymentApp.setSum(BigDecimal.ZERO);
    return paymentApp;
  }

  @Override
  public AcquiringPaymentResponse initPayment(LoanPaymentRequestDto requestDto) {
    try {
      return acquiringService.initPayment(toOrderInitDto(requestDto));
    } catch (Exception e) {
      log.error("error during initPayment {}", e.getMessage());
      return AcquiringPaymentResponse.builder()
          .status(AcquiringBaseStatus.ERROR)
          .message(LoanPaymentConstants.MESSAGE_UNEXPECTED_BEHAVIOUR)
          .build();
    }
  }

  @Override
  public LoanPaymentResponseDto processPayment(LoanPaymentRequestDto requestDto) {
    LoanPaymentResponseDto loanPaymentResponseDto = LoanPaymentResponseDto.builder()
        .paymentTime(LocalDateTime.now())
        .paymentId(requestDto.getPaymentId())
        .build();
    PaymentApp paymentApp = new PaymentApp();
    try {
      fillPaymentRequest(requestDto, paymentApp);
    } catch (Exception e) {
      throw new LoanPaymentException(LoanPaymentConstants.COULD_NOT_CREATE_REQUEST);
    }
    Response response = processRequest(paymentApp);
    if (Objects.nonNull(response) && Objects.nonNull(response.getResult())) {
      if (response.getResult().equals(LoanPaymentConstants.SUCCESS_RESPONSE_RESULT)) {
        loanPaymentResponseDto.setStatus(LoanPaymentStatus.SUCCESS);
        loanPaymentResponseDto.setMessage(response.getComment());
      } else {
        loanPaymentResponseDto.setStatus(LoanPaymentStatus.ERROR);
        loanPaymentResponseDto.setMessage(response.getComment());
      }
    } else {
      loanPaymentResponseDto.setStatus(LoanPaymentStatus.ERROR);
      loanPaymentResponseDto.setMessage(LoanPaymentConstants.MESSAGE_UNEXPECTED_BEHAVIOUR);
    }
    return loanPaymentResponseDto;
  }

  private void fillPaymentRequest(LoanPaymentRequestDto requestDto, PaymentApp paymentApp) {
    paymentApp.setAccount(requestDto.getClientRef());
    paymentApp.setContractNumber(requestDto.getContractNumber());
    paymentApp.setSum(requestDto.getAmount());
    paymentApp.setCommand(LoanPaymentConstants.TYPE_PAY);
    paymentApp.setPayType(BigInteger
        .valueOf(getProductExtRefId(requestDto.getLoanRepayType())));
    paymentApp.setServiceName(mfoAppProperties.getServiceName());
    paymentApp.setTxnDate(mapToGregorianCalendar(String
        .valueOf(LocalDateTime.now())));
    paymentApp.setContractDate(mapToGregorianCalendar(requestDto.getContractDate()));
    if (Arrays.asList(environment.getActiveProfiles()).contains(ACTIVE_PROFILE_PRODUCTION)) {
      paymentApp.setTxnId(String
          .valueOf(requestDto.getPaymentId()));
    } else {
      paymentApp.setTxnId(RandomStringUtils.random(5, true, true));
    }
  }

  @Override
  public LoanPaymentResponseDto retryPayment(Integer paymentId) {
    PaymentEntity paymentEntity = paymentService.getPayment(paymentId);
    if (paymentEntity.getMfoProcessingStatus().equals(MfoProcessingStatus.PROCESSED)) {
      return LoanPaymentResponseDto.builder()
          .message("Платеж принят.")
          .status(LoanPaymentStatus.SUCCESS)
          .paymentId(paymentId)
          .paymentTime(LocalDateTime.now())
          .build();
    }
    LoanPaymentRequestDto paymentDto = modelMapper
        .map(paymentEntity, LoanPaymentRequestDto.class);
    LoanPaymentResponseDto paymentResponse = processPayment(paymentDto);
    paymentService.updateProcessingInfo(paymentId, paymentResponse);

    messageService.fireLoanIinStatusGetEvent(paymentDto.getClientRef(),
        AmqpConfig.LOAN_STATUSES_IIN_ROUTING_KEY);

    return paymentResponse;
  }

  @Override
  public LoanDetailsResponseDto getLoanDetails(LoanDeatilsRequestDto requestDto) {
    PaymentApp paymentApp = fillPaymentAppForCheck(requestDto.getIin());
    LoanInfoDto loanInfoDto = LoanInfoDto.builder().build();
    Response response = processRequest(paymentApp);
    try {
      if (Objects.nonNull(response) && Objects.nonNull(response.getContracts())) {
        Optional<Contract> contract = response.getContracts()
            .getContract()
            .stream()
            .filter(contr -> contr.getContractNumber().equals(requestDto.getContractExtRefId()))
            .findFirst();

        if (contract.isPresent()) {
          loanInfoDto = fillLoanStatus(mapToLoanInfoDto(contract.get()));
        }
      }
    } catch (Exception e) {
      log.error("Error during checkAccount: ", e);
    }
    Optional<OrderEntity> entity = loanOrdersRepository
        .findByOrderIdAndContractExtRefId(
            requestDto.getOrderId(),
            requestDto.getContractExtRefId());
    if (entity.isPresent()) {
      loanInfoDto.setLoanPeriodMonths(entity.get().getLoanPeriodMonths());
      BigDecimal overpayment = loanInfoDto.getAmountOfDebt().subtract(entity.get().getLoanAmount());
      loanInfoDto.setOverpayment(overpayment);
    }
    return LoanDetailsResponseDto.builder()
        .loanInfoDto(loanInfoDto)
        .build();
  }

  private XMLGregorianCalendar mapToGregorianCalendar(String date) {
    return DatatypeFactory.newDefaultInstance()
        .newXMLGregorianCalendar(date);
  }

  private LoanInfoDto mapToLoanInfoDto(Contract contract) {
    return LoanInfoDto.builder()
        .amountOfDebt(contract.getAmountOfDebt()
            .setScale(4, RoundingMode.HALF_UP))
        .arrears(contract.getArrears()
            .setScale(4, RoundingMode.HALF_UP))
        .plannedPaymentAmount(contract.getPlannedPaymentAmount()
            .setScale(4, RoundingMode.HALF_UP))
        .minimumAmountOfPartialRepayment(contract.getMinimumAmountOfPartialRepayment()
            .setScale(4, RoundingMode.HALF_UP))
        .plannedPaymentDate(String.valueOf(contract.getPlannedPaymentDate()))
        .contractDate(String.valueOf(contract.getContractDate()))
        .contractNumber(contract.getContractNumber())
        .build();
  }

  private LoanInfoDto fillLoanStatus(LoanInfoDto loanInfoDto) {
    BigDecimal zero = BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
    LocalDate plannedPaymentDate = LocalDate
        .parse(loanInfoDto.getPlannedPaymentDate(), DATE_TIME_FORMATTER);
    if (!loanInfoDto.getArrears().equals(zero)) {
      loanInfoDto.setLoanStatus(LoanStatusDto.builder()
              .loanStatus(LoanStatus.EXPIRED)
              .description(LoanStatus.EXPIRED.getDescription())
          .build());
      return loanInfoDto;
    }
    if (plannedPaymentDate.minusMonths(1).isAfter(LocalDate.now())) {
      loanInfoDto.setLoanStatus(LoanStatusDto.builder()
          .loanStatus(LoanStatus.PAID)
          .description(LoanStatus.PAID.getDescription())
          .build());
      return loanInfoDto;
    }
    if (plannedPaymentDate.isAfter(LocalDate.now())
        && !loanInfoDto.getPlannedPaymentAmount().equals(zero)) {
      loanInfoDto.setLoanStatus(LoanStatusDto.builder()
          .loanStatus(LoanStatus.PENDING)
          .description(LoanStatus.PENDING.getDescription())
          .build());
      return loanInfoDto;
    }
    loanInfoDto.setLoanStatus(LoanStatusDto.builder()
        .loanStatus(LoanStatus.ACTIVE)
        .description(LoanStatus.ACTIVE.getDescription())
        .build());
    return loanInfoDto;
  }

  private Response processRequest(PaymentApp paymentApp) {
    try {
      return servicesPortType.payment(paymentApp);
    } catch (Exception e) {
      log.error("error during sending request: {}", e.getMessage());
      return null;
    }
  }

  private OrderInitDto toOrderInitDto(LoanPaymentRequestDto requestDto) {
    return OrderInitDto.builder()
        .loanRepayType(requestDto.getLoanRepayType())
        .productExtRefId(String.valueOf(getProductExtRefId(requestDto.getLoanRepayType())))
        .amount(requestDto.getAmount())
        .contractDate(requestDto.getContractDate())
        .orderTime(LocalDateTime.now())
        .contractNumber(requestDto.getContractNumber())
        .iin(requestDto.getClientRef())
        .build();
  }

  private Long getProductExtRefId(LoanRepayType loanRepayType) {
    switch (loanRepayType) {
      case PLANNED_REPAYMENT:
        return loanPaymentServices.getPlannedRePaymentProductId();
      case PARTIAL_REPAYMENT:
        return loanPaymentServices.getPartialRePaymentProductId();
      case TOTAL_REPAYMENT:
        return loanPaymentServices.getTotalRePaymentProductId();
      default:
        return 1L;
    }
  }
}
