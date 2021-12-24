package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.IllegalApiUsageGeneralApiException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.component.DocumentGenerator;
import kz.codesmith.epay.loan.api.domain.orders.OrderEntity;
import kz.codesmith.epay.loan.api.model.AlternativeChoiceDto;
import kz.codesmith.epay.loan.api.model.documents.LoanDebtorFormPdfDto;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.halyk.HalyCallbackRequestDto;
import kz.codesmith.epay.loan.api.model.halyk.HalykCardCashoutResponseDto;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.orders.OrderState;
import kz.codesmith.epay.loan.api.model.orders.OrderType;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.model.scoring.ScoringInfo;
import kz.codesmith.epay.loan.api.model.scoring.ScoringRequest;
import kz.codesmith.epay.loan.api.repository.LoanOrdersRepository;
import kz.codesmith.epay.loan.api.service.IDocumentCreatePdf;
import kz.codesmith.epay.loan.api.service.ILoanOrdersService;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.codesmith.epay.loan.api.service.IPayoutService;
import kz.codesmith.epay.loan.api.service.StorageService;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.codesmith.logger.Logged;
import kz.payintech.ListLoanMethod;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanOrdersService implements ILoanOrdersService {

  private static final String CASH_OUT_INIT_MAP_KEY = "cashout_init";
  private static final String CASH_OUT_RESULT_MAP_KEY = "cashout_result";
  private final UserContextHolder userContext;
  private final LoanOrdersRepository loanOrdersRepository;
  private final ModelMapper mapper;
  private final ObjectMapper objectMapper;
  private final IMfoCoreService mfoCoreService;
  private final StorageService storageService;
  private final IDocumentCreatePdf createPdf;
  private final DocumentGenerator docGenerator;

  @Override
  public Page<OrderDto> getOrdersByUserOwner(LocalDate startDate, LocalDate endDate,
      Integer orderId, List<OrderState> states, Pageable pageRequest) {
    var currentUserContext = this.userContext.getContext();
    if (CollectionUtils.isEmpty(states)) {
      states = Arrays.asList(OrderState.values());
    }
    switch (currentUserContext.getOwnerType()) {
      case OPERATOR:
      case AGENT:
        return loanOrdersRepository.findAllByInsertedTimeIsBetweenAndState(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                orderId,
                states,
                pageRequest
        ).map(o -> mapper.map(o, OrderDto.class));
      case CLIENT:
        return loanOrdersRepository.findAllByInsertedTimeIsBetweenAndClientAndState(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX),
                orderId,
                currentUserContext.getOwnerId(),
                states,
                pageRequest
        ).map(o -> mapper.map(o, OrderDto.class));
      default:
        return Page.empty();
    }

  }

  @Override
  public Page<OrderDto> getOrdersByUserOwner(
      LocalDate startDate,
      LocalDate endDate,
      Integer orderId,
      Pageable pageRequest
  ) {

    var currentUserContext = this.userContext.getContext();
    switch (currentUserContext.getOwnerType()) {
      case OPERATOR:
      case AGENT:
        return loanOrdersRepository.findAllByInsertedTimeIsBetween(
            startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX),
            orderId,
            pageRequest
        ).map(o -> mapper.map(o, OrderDto.class));
      case CLIENT:
        return loanOrdersRepository.findAllByInsertedTimeIsBetweenAndClient(
            startDate.atStartOfDay(),
            endDate.atTime(LocalTime.MAX),
            currentUserContext.getOwnerId(),
            orderId,
            pageRequest
        ).map(o -> mapper.map(o, OrderDto.class));
      default:
        return Page.empty();
    }
  }

  @Override
  public OrderDto getOrderByUserOwner(Integer orderId) {
    var currentUserContext = this.userContext.getContext();
    switch (currentUserContext.getOwnerType()) {
      case AGENT:
      case OPERATOR:
        return loanOrdersRepository.findById(orderId)
            .map(o -> mapper.map(o, OrderDto.class))
            .orElse(null);
      case CLIENT:
        return loanOrdersRepository.findByOrderIdAndClientId(
            orderId,
            currentUserContext.getOwnerId()
        ).map(o -> mapper.map(o, OrderDto.class))
            .orElse(null);
      default:
        throw new IllegalApiUsageGeneralApiException(currentUserContext.getOwnerType().name());
    }
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto createNewPrimaryLoanOrder(ScoringRequest request) {
    var userContext = this.userContext.getContext();
    var orderEntity = new OrderEntity();
    orderEntity.setClientId(userContext.getOwnerId());
    orderEntity.setIpAddress(userContext.getIpAddress());
    orderEntity.setIin(request.getIin());
    orderEntity.setLoanAmount(BigDecimal.valueOf(request.getLoanAmount()));
    orderEntity.setLoanEffectiveRate(orderEntity.getLoanEffectiveRate());
    orderEntity.setLoanMethod(request.getLoanMethod().name());
    orderEntity.setLoanPeriodMonths(request.getLoanPeriod());
    orderEntity.setLoanProduct(request.getLoanMethod().equals(ListLoanMethod.ANNUITY_PAYMENTS)
        ? "000000004" : "000000005");
    orderEntity.setMsisdn(userContext.getUsername());
    orderEntity.setOrderType(OrderType.PRIMARY);
    orderEntity.setStatus(OrderState.NEW);
    orderEntity.setPreScoreRequestId(request.getPreScoreRequestId());
    orderEntity.setClientInfo(getClientInfo(request.getPersonalInfo()));
    orderEntity.setPersonalInfo(objectMapper.convertValue(
        request.getPersonalInfo(),
        new TypeReference<>() {
        })
    );
    orderEntity = loanOrdersRepository.save(orderEntity);
    return mapper.map(orderEntity, OrderDto.class);
  }

  private String getClientInfo(PersonalInfoDto personalInfo) {

    var builder = new StringBuilder();

    builder.append(StringUtils.capitalize(personalInfo.getLastName().toLowerCase()))
        .append(" ")
        .append(StringUtils.capitalize(personalInfo.getFirstName().toLowerCase()));

    if (StringUtils.isNotBlank(personalInfo.getMiddleName())) {
      builder.append(" ").append(personalInfo.getMiddleName());
    }

    return builder.toString();
  }

  @Logged
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public List<AlternativeChoiceDto> createNewAlternativeLoanOrders(
      OrderDto order,
      List<AlternativeChoiceDto> alternatives
  ) {
    var userContext = this.userContext.getContext();
    alternatives.forEach(alternative -> {
      OrderEntity orderEntity = new OrderEntity();
      orderEntity.setClientId(userContext.getOwnerId());
      orderEntity.setClientInfo(order.getClientInfo());
      orderEntity.setParentOrderId(order.getOrderId());
      orderEntity.setIin(order.getIin());
      orderEntity.setLoanAmount(alternative.getLoanAmount());
      orderEntity.setLoanInterestRate(alternative.getLoanInterestRate());
      orderEntity.setLoanEffectiveRate(order.getLoanEffectiveRate());
      orderEntity.setLoanMethod(order.getLoanMethod());
      orderEntity.setScoringInfo(objectMapper.convertValue(
          order.getScoringInfo(),
          new TypeReference<>() {
          })
      );
      orderEntity.setLoanPeriodMonths(alternative.getLoanMonthPeriod());
      orderEntity
          .setLoanProduct(order.getLoanMethod().equals(ListLoanMethod.ANNUITY_PAYMENTS.name())
              ? "000000004" : "000000005");
      orderEntity.setMsisdn(userContext.getUsername());
      orderEntity.setOrderType(OrderType.ALTERNATIVE);
      orderEntity.setStatus(OrderState.APPROVED);
      orderEntity.setPersonalInfo(order.getPersonalInfo());
      orderEntity = loanOrdersRepository.save(orderEntity);
      alternative.setOrderId(orderEntity.getOrderId());
      alternative.setOrderTime(orderEntity.getInsertedTime());

      var newOrder = SerializationUtils.clone(order);
      newOrder.setLoanAmount(alternative.getLoanAmount());
      newOrder.setLoanPeriodMonths(alternative.getLoanMonthPeriod());

      var orderResponse = mfoCoreService.getNewOrder(newOrder);
      orderEntity.setOrderExtRefId(orderResponse.getNumber());
      orderEntity.setOrderExtRefTime(orderResponse.getDateTime());

      newOrder.setLoanInterestRate(alternative.getLoanInterestRate());
      newOrder.setOrderExtRefId(orderResponse.getNumber());
      newOrder.setOrderExtRefTime(orderResponse.getDateTime());
      var contract = mfoCoreService.getNewContract(newOrder);
      log.info("client {} new contract number for alternative is, {}", order.getIin(),
          contract.getNumber());
      orderEntity.setContractExtRefId(contract.getNumber());
      var key = newOrder.getIin() + "/contract-" + newOrder.getOrderId() + "-"
          + newOrder.getOrderExtRefId() + "-" + newOrder.getOrderExtRefTime() + ".pdf";
      storageService.put(
          key,
          new ByteArrayInputStream(Base64.decodeBase64(contract.getContract())),
          MediaType.APPLICATION_PDF_VALUE
      );
      orderEntity.setContractDocumentS3Key(key);
      orderEntity.setContractExtRefTime(contract.getDateTime());
    });

    return alternatives;
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto updateLoanOrderStatus(Integer orderId, OrderState status) {
    var order = getOrder(orderId);
    order.setStatus(status);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto updateLoanOrderStatusAndLoanEffectiveRate(
      Integer orderId,
      OrderState status,
      BigDecimal interestRate,
      BigDecimal loanEffectiveRate
  ) {
    var order = getOrder(orderId);
    order.setStatus(status);
    order.setLoanEffectiveRate(loanEffectiveRate);
    order.setLoanInterestRate(interestRate);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto rejectLoanOrder(Integer orderId, String rejectReason) {
    return rejectLoanOrder(orderId, OrderState.REJECTED, rejectReason);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto rejectLoanOrder(Integer orderId, OrderState status, String rejectReason) {
    var order = getOrder(orderId);
    order.setStatus(status);
    order.setScoringRejectReason(rejectReason);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto updateLoanOrderExtRefs(
      Integer orderId,
      String orderExtRefId,
      LocalDateTime orderExtRefTime
  ) {
    var order = getOrder(orderId);
    order.setOrderExtRefId(orderExtRefId);
    order.setOrderExtRefTime(orderExtRefTime);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Override
  public OrderDto updateLoanOrderContractRefs(
      Integer orderId,
      String contractDocumentS3Key,
      String contractExtRefId,
      LocalDateTime contractExtRefTime
  ) {
    var order = getOrder(orderId);
    order.setContractDocumentS3Key(contractDocumentS3Key);
    order.setContractExtRefId(contractExtRefId);
    order.setContractExtRefTime(contractExtRefTime);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderDto updateLoanOrderCashoutCardInitInfo(
      Integer orderId,
      OrderState status,
      HalykCardCashoutResponseDto halykCardTransfer
  ) {
    var order = getOrder(orderId);
    order.getCashOutInfo().put(
        CASH_OUT_INIT_MAP_KEY,
        objectMapper.convertValue(halykCardTransfer, new TypeReference<>() {
        })
    );
    order.setStatus(status);
    return mapper.map(order, OrderDto.class);
  }

  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderDto updateLoanOrderCashoutCardBankResponseInfo(
      Integer orderId,
      OrderState status,
      HalyCallbackRequestDto request
  ) {
    var order = getOrder(orderId);
    order.getCashOutInfo().put(
        CASH_OUT_RESULT_MAP_KEY,
        objectMapper.convertValue(request, new TypeReference<>() {
        })
    );
    order.setStatus(status);

    if (OrderState.CASHED_OUT_CARD.equals(status) || OrderState.CASHED_OUT_WALLET.equals(status)) {
      var entity = getOrder(orderId);
      var orderDto = mapper.map(entity, OrderDto.class);
      mfoCoreService.sendBorrowerSignature(orderDto);
    }
    return mapper.map(order, OrderDto.class);
  }

  @Transactional
  @Override
  public OrderDto approveOrder(Integer orderId) {
    var order = getOrderByUserOwner(orderId);
    if (OrderState.APPROVED.equals(order.getStatus())) {
      return updateLoanOrderStatus(orderId, OrderState.CONFIRMED);
    } else {
      throw new MfoGeneralApiException("Incorrect state " + order.getStatus());
    }
  }

  @Override
  public List<AlternativeChoiceDto> getAlternativeChoices(OrderDto order) {
    var userContext = this.userContext.getContext();
    if (OrderState.ALTERNATIVE.equals(order.getStatus())) {
      var alternatives = loanOrdersRepository.findAllByParentOrderIdAndClientId(
          order.getOrderId(),
          userContext.getOwnerId()
      );
      return alternatives.stream()
          .map(o -> AlternativeChoiceDto.builder()
              .loanAmount(o.getLoanAmount())
              .loanMonthPeriod(o.getLoanPeriodMonths())
              .orderId(o.getOrderId())
              .orderTime(o.getInsertedTime())
              .build()
          ).collect(Collectors.toList());
    } else {
      return Collections.emptyList();
    }

  }

  @Logged
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderDto updateScoringInfo(Integer orderId, ScoringInfo scoringInfo) {
    var order = getOrder(orderId);
    order.setScoringInfo(objectMapper.convertValue(
        scoringInfo,
        new TypeReference<>() {
        })
    );
    if (Objects.nonNull(scoringInfo.getIncomesInfo())) {
      order.setIncomesInfo(scoringInfo.getIncomesInfo());
    }
    loanOrdersRepository.save(order);
    return mapper.map(order, OrderDto.class);
  }

  @SneakyThrows
  @Override
  public byte[] getOrderContractDocument(Integer orderId) {
    var order = getOrder(orderId);
    if (Objects.isNull(order)) {
      throw new MfoGeneralApiException("Order Not Found");
    }
    if (StringUtils.isBlank(order.getContractDocumentS3Key())) {
      throw new MfoGeneralApiException("Contract Not Found");
    }
    return IOUtils.readBytesFromStream(storageService.get(order.getContractDocumentS3Key()));
  }

  @Override
  @SneakyThrows
  public byte[] getLoanDebtorFormPdf(Integer orderId) {
    OrderEntity order = getOrder(orderId);
    return docGenerator.generatePdf("loan-application", mapOrderEntity(order));
  }

  @Override
  public void updateLoanOrderIdentityMatchResult(Integer orderId, Double result) {
    OrderEntity order = getOrder(orderId);
    order.setFaceMatching(result);
    loanOrdersRepository.save(order);
  }

  @Override
  public List<OrderDto> findAllOpenAlternativeLoansByIin(String clientIin) {
    List<OrderState> states = Arrays
        .asList(OrderState.CASHED_OUT_WALLET, OrderState.CASHED_OUT_CARD);
    return loanOrdersRepository
        .findAllByIinAndStatusInAndOrderType(clientIin, states, OrderType.ALTERNATIVE)
        .stream()
        .map(o -> mapper.map(o, OrderDto.class))
        .collect(Collectors.toList());
  }

  @Logged
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderDto updateScoringInfoAndEffectiveRateValues(Integer orderId, ScoringInfo scoringInfo,
      BigDecimal loanEffectiveRate) {
    var order = getOrder(orderId);
    order.setScoringInfo(objectMapper.convertValue(
        scoringInfo,
        new TypeReference<>() {
        })
    );
    order.setLoanEffectiveRate(loanEffectiveRate);
    loanOrdersRepository.save(order);
    return mapper.map(order, OrderDto.class);
  }

  @Logged
  @Transactional(isolation = Isolation.SERIALIZABLE)
  @Override
  public OrderDto updateEffectiveRateAndInterestRateValues(Integer orderId, Float loanEffectiveRate,
      Float interestRate) {
    var order = getOrder(orderId);
    order.setLoanEffectiveRate(BigDecimal.valueOf(loanEffectiveRate));
    order.setLoanInterestRate(BigDecimal.valueOf(interestRate));
    loanOrdersRepository.save(order);
    return mapper.map(order, OrderDto.class);
  }

  @Override
  public OrderDto updateLoanOrderPayoutResponseInfo(Integer orderId, OrderState status,
      String body) {
    var order = getOrder(orderId);
    if (OrderState.CASHED_OUT_CARD.equals(order.getStatus())) {
      return mapper.map(order, OrderDto.class);
    }
    order.getCashOutInfo().put(
        CASH_OUT_RESULT_MAP_KEY,
        objectMapper.convertValue(body, new TypeReference<>() {
        })
    );
    order.setStatus(status);
    loanOrdersRepository.save(order);
    if (OrderState.CASHED_OUT_CARD.equals(status) || OrderState.CASHED_OUT_WALLET.equals(status)) {
      var entity = getOrder(orderId);
      var orderDto = mapper.map(entity, OrderDto.class);
      mfoCoreService.sendBorrowerSignature(orderDto);
    }
    return mapper.map(order, OrderDto.class);
  }

  @Override
  public List<OrderDto> getAllCashedOutInitializedOrders() {
    return loanOrdersRepository.findAllByStatusAndTime(
        IPayoutService.initPayoutStates,
        LocalDateTime.now().minusDays(1),
        LocalDateTime.now())
        .stream()
        .map(entity -> mapper.map(entity, OrderDto.class))
        .collect(Collectors.toList());
  }

  private Map<String, Object> mapOrderEntity(OrderEntity order) {
    var valuesMap = new HashMap<String, Object>();
    var dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    var personalInfoMap = order.getPersonalInfo();

    var date = order.getInsertedTime();
    valuesMap.put("date", dateFormatter.format(date));
    valuesMap.put("name", order.getClientInfo());
    valuesMap.put("loanPurpose", "Нецелевое/ Мақсатты емес");
    valuesMap.put("loanSecurity", "Без обеспечения (беззалоговый)/ Кепілдіксіз");
    valuesMap.put("amount", order.getLoanAmount());
    valuesMap.put("period", order.getLoanPeriodMonths());
    valuesMap.put("overpaymentAmount", "");
    valuesMap.put("fee", "");
    var method = Objects.equals(
        order.getLoanMethod(), ListLoanMethod.ANNUITY_PAYMENTS.name())
        ? "аннуитетный / аннуитетті"
        : "дифференцированный / сараланған";
    valuesMap.put("method", method);

    valuesMap.put("iin", order.getIin());
    var birtDate = LocalDate.parse(personalInfoMap.get("birthDate").toString());
    valuesMap.put("birthDate", dateFormatter.format(birtDate));
    valuesMap.put("gender", personalInfoMap.get("gender"));
    var document = (Map<String, String>) personalInfoMap.get("nationalIdDocument");
    valuesMap.put("docNum", document.get("idNumber"));
    valuesMap.put("docDate", dateFormatter.format(LocalDate.parse(document.get("issuedDate"))));
    valuesMap.put("docExpire", dateFormatter.format(LocalDate.parse(document.get("expireDate"))));
    valuesMap.put("docIssuer", document.get("issuedBy"));
    valuesMap.put("maritalStatus", personalInfoMap.get("maritalStatus"));
    valuesMap.put("children", personalInfoMap.get("numberOfKids"));
    valuesMap.put("education", personalInfoMap.get("education"));
    valuesMap.put("workPlace", personalInfoMap.get("employer"));
    valuesMap.put("jobPosition", personalInfoMap.get("workPosition"));
    valuesMap.put("foreignOfficial", "");
    valuesMap.put("monthlyIncome", personalInfoMap.get("monthlyIncome"));
    valuesMap.put("additionalIncome", personalInfoMap.get("additionalMonthlyIncome"));
    valuesMap.put("msisdn", order.getMsisdn());
    valuesMap.put("additionalContacts", "");
    var addressMap = (Map<String, String>) personalInfoMap.get("registrationAddress");
    var address = addressMap.get("city") + "\n"
        + addressMap.get("district") + "\n"
        + addressMap.get("street") + "\n"
        + addressMap.get("house") + "\n"
        + addressMap.get("apartment");
    valuesMap.put("registrationAddress", address);
    valuesMap.put("card", "");

    return valuesMap;
  }

  private LoanDebtorFormPdfDto mapOrderEntityToLoanDebtFormDto(OrderEntity entity) {
    Map<String, Object> personalInfoMap = entity.getPersonalInfo();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    LoanDebtorFormPdfDto dto = new LoanDebtorFormPdfDto();
    dto.setLoanPurpose("Нецелевое/ Мақсатты емес");
    dto.setLoanSecurity("Без обеспечения (беззалоговый)/ Кепілдіксіз");
    dto.setIdType("Удостоверение личности");
    dto.setLoanMethod("Электронный/ Электронды");
    dto.setFio(entity.getClientInfo());
    dto.setIin(entity.getIin());
    dto.setBirthdate(
        formatter.format(LocalDate.parse(personalInfoMap.get("birthDate").toString())));
    dto.setGender(personalInfoMap.get("gender").toString());
    dto.setMaritalStatus(personalInfoMap.get("maritalStatus").toString());
    dto.setNumberOfKids(personalInfoMap.get("numberOfKids").toString());
    dto.setWorkplace(personalInfoMap.get("employer").toString());
    dto.setWorkPosition(personalInfoMap.get("workPosition").toString());
    dto.setIncome(personalInfoMap.get("monthlyIncome").toString());
    dto.setNumbersInString(
        entity.getMsisdn() + "\n" + personalInfoMap.get("workPhoneNum").toString()
    );
    dto.setLoanMethodRuKzPair(Objects.equals(
        entity.getLoanMethod(), ListLoanMethod.ANNUITY_PAYMENTS.name())
        ? Pair.of("аннуитетный", "аннуитетті")
        : Pair.of("дифференцированный", "сараланған"));

    Map<String, String> addressMap =
        (Map<String, String>) personalInfoMap.get("residenceAddress");

    Map<String, String> idDocumentInfoMap =
        (Map<String, String>) personalInfoMap.get("nationalIdDocument");

    dto.setIdInfo(idDocumentInfoMap.get("idNumber") + "\n"
        + formatter.format(LocalDate.parse(idDocumentInfoMap.get("issuedDate"))) + "\n"
        + formatter.format(LocalDate.parse(idDocumentInfoMap.get("expireDate"))) + "\n"
        + idDocumentInfoMap.get("issuedBy")
    );
    dto.setAddress(addressMap.get("city") + "\n"
        + addressMap.get("district") + "\n"
        + addressMap.get("street") + "\n"
        + addressMap.get("house") + "\n"
        + addressMap.get("apartment")
    );
    // TODO set email dto.setEmail() when such field will appear in db
    return dto;
  }

  private OrderEntity getOrder(Integer orderId) {
    return loanOrdersRepository.findById(orderId)
        .orElseThrow(() -> new NotFoundApiServerException(
            ApiErrorTypeParamValues.ORDER,
            orderId
        ));
  }
}
