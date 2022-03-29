package kz.codesmith.epay.loan.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.orders.OrderDto;
import kz.codesmith.epay.loan.api.model.scoring.AddressDto;
import kz.codesmith.epay.loan.api.model.scoring.PersonalInfoDto;
import kz.codesmith.epay.loan.api.service.IMfoCoreService;
import kz.payintech.Address;
import kz.payintech.Client;
import kz.payintech.ContactFaces;
import kz.payintech.InformationTheAgreement;
import kz.payintech.ListLoanMethod;
import kz.payintech.LoanSchedule;
import kz.payintech.NewOrder;
import kz.payintech.ResultDataNumberDate;
import kz.payintech.ResultDataPDF;
import kz.payintech.SummPaymentSchedule;
import kz.payintech.TotalDebt;
import kz.payintech.UserOrderList;
import kz.payintech.siteexchange.SiteExchangePortType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MfoCoreService implements IMfoCoreService {

  private final SiteExchangePortType wsMfoCoreService;
  private final ObjectMapper objectMapper;

  @Override
  public List<SummPaymentSchedule> getSummPaymentSchedule(String iin) {
    var response = wsMfoCoreService.getSummPaymentSchedule(iin);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public ResultDataPDF getNewContract(OrderDto order) {
    var request = new ResultDataNumberDate();
    request.setNumber(order.getOrderExtRefId());
    request.setDateTime(order.getOrderExtRefTime());

    log.info(
        "getNewContract orderId={} iin={} derExtRefId={} loanInterestRate={}",
        order.getOrderId(),
        order.getIin(),
        order.getOrderExtRefId(),
        order.getLoanInterestRate()
    );
    return getNewContract(request, order.getLoanInterestRate().floatValue());
  }

  @Override
  public ResultDataPDF getNewContract(
      ResultDataNumberDate dataNumberDate,
      float loanInterestRate
  ) {
    var response = wsMfoCoreService.getNewContract(dataNumberDate, loanInterestRate);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public List<UserOrderList> getUserOrderList(String iin) {
    var response = wsMfoCoreService.getUserOrderList(iin);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public LoanSchedule getLoanScheduleCalculation(
      BigDecimal amount,
      Integer loanMonthPeriod,
      float loanInterestRate,
      String creditProduct,
      ListLoanMethod loanType
  ) {
    var response = wsMfoCoreService.getLoanScheduleCalculation(
        amount,
        BigInteger.valueOf(loanMonthPeriod),
        loanInterestRate,
        creditProduct,
        loanType,
        LocalDate.now()
    );
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public TotalDebt getTotalDebt(String iin) {
    var response = wsMfoCoreService.getTotalDebt(iin);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public List<InformationTheAgreement> getUserContractsList(String iin) {
    var response = wsMfoCoreService.getUserContractsList(iin);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public void sendBorrowerSignature(OrderDto order) {
    var contractSignRequest = new ResultDataNumberDate();
    contractSignRequest.setNumber(order.getOrderExtRefId());
    contractSignRequest.setDateTime(order.getOrderExtRefTime());
    sendBorrowerSignature(contractSignRequest);
  }

  @Override
  public void sendBorrowerSignature(ResultDataNumberDate dataNumberDate) {
    var response = wsMfoCoreService.sendBorrowerSignature(dataNumberDate);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public ResultDataNumberDate getNewOrder(NewOrder newOrder) {
    var response = wsMfoCoreService.getNewOrder(newOrder);
    if (Objects.nonNull(response)) {
      if (StringUtils.isNotBlank(response.getError())) {
        throw new MfoGeneralApiException(response.getError());
      }
      return response.getData();
    } else {
      throw new MfoGeneralApiException("no response");
    }
  }

  @Override
  public ResultDataNumberDate getNewOrder(OrderDto order) {
    var client = new Client();
    var personalInfo = objectMapper.convertValue(order.getPersonalInfo(), PersonalInfoDto.class);
    client.setBankAccountNumber("");
    client.setBankBIC("");
    client.setBankName("");
    client.setBirthday(personalInfo.getBirthDate());
    var colleague = new ContactFaces();
    colleague.setNameContact("");
    colleague.setPhoneContact("");
    client.setColleague(colleague);
    client.setDohod(personalInfo.getMonthlyIncome().toBigInteger());
    client.setEmail("");
    client.setGender(personalInfo.getGender());
    client.setIDCard(personalInfo.getNationalIdDocument().getIdNumber());
    client.setIDCardDateIssue(personalInfo.getNationalIdDocument().getIssuedDate());
    client.setIDCardIssuingAuthority(personalInfo.getNationalIdDocument().getIssuedBy());
    client.setIDCardValidity(personalInfo.getNationalIdDocument().getExpireDate());
    client.setIDSeries(personalInfo.getNationalIdDocument().getIdNumber());
    client.setIDType("УдостоверениеЛичностиРК");
    client.setIIN(order.getIin());
    client.setMiddlename(personalInfo.getMiddleName());
    client.setName(personalInfo.getFirstName());
    client.setPhone(order.getMsisdn());
    client.setRegionKATO(personalInfo.getRegistrationAddress().getRegion());
    client.setRegistrationAddress(getAddress(personalInfo.getRegistrationAddress()));
    var relative = new ContactFaces();
    relative.setNameContact("");
    relative.setPhoneContact("");
    client.setRelative(relative);
    client.setResidenceAddress(getAddress(personalInfo.getResidenceAddress()));
    client.setSurname(personalInfo.getLastName());
    var work = mapTypeOfWorkTo1CMfoTypes(personalInfo);
    client.setWork(work);
    client.setWorkaddress(personalInfo.getWorkPhoneNum());
    client.setWorkname(personalInfo.getEmployer());
    client.setWorkphone(personalInfo.getWorkPhoneNum());
    client.setWorkposition(personalInfo.getWorkPosition());
    var orderRequest = new NewOrder();
    orderRequest.setClient(client);
    orderRequest.setCredProd(order.getLoanProduct());
    orderRequest.setLoanTerm(BigInteger.valueOf(order.getLoanPeriodMonths()));
    orderRequest.setLoanAmount(order.getLoanAmount().toBigInteger());
    return getNewOrder(orderRequest);
  }

  // not the most elegant solution but will work for now
  private String mapTypeOfWorkTo1CMfoTypes(PersonalInfoDto personalInfo) {
    var work = personalInfo.getEmployment();
    if (StringUtils.isNotEmpty(work)) {
      if ("Безработный".equalsIgnoreCase(work) || "Студент".equalsIgnoreCase(work)) {
        work = "НеРаботает";
      } else if ("Сотрудник компании".equalsIgnoreCase(work)) {
        work = "Работает";
      } else if ("Индивидуальный предприниматель".equalsIgnoreCase(work)) {
        work = "Самозанятый";
      } else if (work.startsWith("Пенсионер")) {
        work = "Пенсионер";
      } else if ("Владелец компании".equalsIgnoreCase(work)) {
        work = "СобственныйБизнес";
      } else if ("В декретном отпуске".equalsIgnoreCase(work)) {
        work = "Домохозяйка";
      }
    }

    return work;
  }

  private Address getAddress(AddressDto addressDto) {
    var address = new Address();
    address.setApartment(addressDto.getApartment());
    address.setCity(addressDto.getCity());
    address.setCountry(addressDto.getCountry());
    address.setHouse(addressDto.getHouse());
    address.setRegion(addressDto.getRegion());
    address.setStreet(addressDto.getStreet());
    return address;
  }

}
