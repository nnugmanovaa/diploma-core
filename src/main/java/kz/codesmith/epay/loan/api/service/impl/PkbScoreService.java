package kz.codesmith.epay.loan.api.service.impl;

import com.creditinfo.ws.kdn2.CigWsHeader;
import com.creditinfo.ws.kdn2.KdnRequest;
import com.creditinfo.ws.kdn2.KdnService;
import com.creditinfo.ws.kdn2.StoreKDNReq;
import com.creditinfo.ws.score.ScoreAttribute;
import com.creditinfo.ws.score.ScoreCard;
import com.creditinfo.ws.score.ScoreData;
import com.creditinfo.ws.score.ScoreService;
import com.fcb.closedcontracts.service.web.GetContractSum;
import com.fcb.closedcontracts.service.web.GetContractSumResponse;
import com.fcb.closedcontracts.service.web.ServiceReturn;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import kz.codesmith.epay.loan.api.component.PkbCheckRs;
import kz.codesmith.epay.loan.api.configuration.RedisCacheConfig;
import kz.codesmith.epay.loan.api.configuration.pkb.PkbProperties;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.pkb.ws.ApplicationReportDto;
import kz.codesmith.epay.loan.api.model.pkb.ws.KdnReqResponseDto;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.logger.Logged;
import kz.com.fcb.fico.CigHeader;
import kz.com.fcb.fico.Result;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Logged
@Service
@RequiredArgsConstructor
public class PkbScoreService implements IPkbScoreService {

  private final ScoreService wsPkbBehaviorService;
  private final KdnService wsPkbKdnService;
  private final PkbProperties pkbProperties;
  private final com.fcb.closedcontracts.service.web.Service pkbClosedContractService;
  private final kz.com.fcb.fico.ScoreService wsPkbFicoService;
  private final ModelMapper modelMapper;
  private final PkbCheckRs pkbCheckRs;

  @Override
  public List<ScoreCard> getScoreCards() {
    return wsPkbBehaviorService.getScoreCards();
  }

  @Cacheable(value = RedisCacheConfig.PKB_CACHE_NAME, unless = "#result?.errorCode == -1000")
  @Override
  public ScoreData getBehaviorScore(String iin, boolean consentConfirmed) {
    var consentAttribute = new ScoreAttribute();
    consentAttribute.setName("ConsentConfirmed");
    consentAttribute.setValue(consentConfirmed ? "1" : "0");
    var iinAttribute = new ScoreAttribute();
    iinAttribute.setName("IIN");
    iinAttribute.setValue(iin);
    return wsPkbBehaviorService.score("BehaviorScoring", List.of(consentAttribute, iinAttribute));
  }

  @Cacheable(
      value = RedisCacheConfig.PKB_CACHE_NAME,
      unless = "#result?.errorCode == -1000 || #result?.errorCode == -1001"
          + " || #result?.errorCode == -1002 || #result?.errorCode == -1003"
          + " || #result?.errorCode == -1004"
  )
  @SneakyThrows
  @Override
  public Result getFicoScore(String iin) {
    var cigHeader = new CigHeader();
    cigHeader.setUser(pkbProperties.getUsername());
    cigHeader.setPassword(pkbProperties.getPassword());
    return wsPkbFicoService.getScore(cigHeader, iin);
  }

  @Logged
  @SneakyThrows
  @Cacheable(value = RedisCacheConfig.PKB_CACHE_NAME, unless = "#result == null")
  @Override
  public ApplicationReportDto getKdnScore(
      String iin,
      LocalDate birthDate,
      boolean consentConfirmed,
      String firstName,
      String lastName,
      String middleName
  ) {
    var kdnRequest = new KdnRequest();
    kdnRequest.setIIN(iin);
    kdnRequest.setBirthdate(Optional.ofNullable(birthDate)
        .map(LocalDate::toString)
        .orElse(null));
    kdnRequest.setConsentConfirmed(consentConfirmed ? 1 : 0);
    kdnRequest.setFirstname(firstName);
    kdnRequest.setLastname(lastName);
    kdnRequest.setMiddlename(middleName);
    var storeKdnReq = new StoreKDNReq();
    storeKdnReq.setApplicationReq(kdnRequest);
    var cigWsHeader = new CigWsHeader();
    cigWsHeader.setCulture("RU");
    cigWsHeader.setUserName(pkbProperties.getUsername());
    cigWsHeader.setPassword(pkbProperties.getPassword());
    var response = wsPkbKdnService.storekdnReqV2(storeKdnReq, cigWsHeader);
    if (Objects.isNull(response) || Objects.isNull(response.getReturn())) {
      throw new MfoGeneralApiException("KDN score is unavailable");
    }
    if (response.getReturn().getErrorCode() < 0) {
      throw new MfoGeneralApiException(response.getReturn().getErrorMessage());
    }
    var resp = modelMapper.map(response.getReturn(), KdnReqResponseDto.class)
        .getApplicationReport();
    // TODO fix caching issue
    resp.getIncomesResultCrtrV2().getResultASP().setPersonLargeFamilyResponse(null);
    resp.getIncomesResultCrtrV2().getResultESP().setSingleAggregatePaymentResponse(null);
    return resp;
  }

  @Cacheable(
      value = RedisCacheConfig.PKB_CACHE_NAME,
      unless = "#result?.statusCode == 1"
  )
  @SneakyThrows
  @Override
  public ServiceReturn getContractSum(String iin, boolean consentConfirmed) {
    var cigWsHeader = new com.fcb.closedcontracts.service.web.CigWsHeader();
    cigWsHeader.setCulture("RU");
    cigWsHeader.setPassword(pkbProperties.getPassword());
    cigWsHeader.setUserName(pkbProperties.getUsername());
    cigWsHeader.setSecurityToken("0");
    cigWsHeader.setUserId(0);
    cigWsHeader.setVersion("1");
    var contractSum = new GetContractSum();
    contractSum.setConsentConfirmed(consentConfirmed ? "1" : "0");
    contractSum.setIIN(iin);
    return Optional.ofNullable(pkbClosedContractService.getContractSum(contractSum, cigWsHeader))
        .map(GetContractSumResponse::getReturn)
        .orElse(null);
  }

  @Cacheable(
      value = RedisCacheConfig.PKB_STOP_FACTOR_CACHE_NAME,
      key = "{#iin}",
      unless = "#result == null"
  )
  @Override
  public String getCustomerInfoByIin(String iin) {
    log.info("PKB GET STOP-FACTORS for {}", iin);
    return pkbCheckRs.getCustomerInfoByIin(iin);
  }

}
