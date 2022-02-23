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
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import kz.codesmith.epay.loan.api.component.PkbCheckRs;
import kz.codesmith.epay.loan.api.configuration.PkbConnectorProperties;
import kz.codesmith.epay.loan.api.configuration.RedisCacheConfig;
import kz.codesmith.epay.loan.api.configuration.pkb.PkbProperties;
import kz.codesmith.epay.loan.api.model.PkbReportsDto;
import kz.codesmith.epay.loan.api.model.PkbReportsRequest;
import kz.codesmith.epay.loan.api.model.exception.KdnReportFailedException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.model.exception.PkbConnectorReportsFailedException;
import kz.codesmith.epay.loan.api.model.pkb.kdn.ApplicationReport;
import kz.codesmith.epay.loan.api.model.pkb.ws.ApplicationReportDto;
import kz.codesmith.epay.loan.api.model.pkb.ws.KdnReqResponseDto;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.logger.Logged;
import kz.com.fcb.fico.CigHeader;
import kz.com.fcb.fico.Result;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
  private final PkbConnectorProperties pkbConnectorProperties;
  private final RestTemplate restTemplate;

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

  @Override
  public ApplicationReport getKdnReport(
      kz.codesmith.epay.loan.api.model.pkb.kdn.KdnRequest kdnRequest) {
    var url = pkbConnectorProperties.getUrl() + "/kdn";
    var requestEntity = HttpEntity.EMPTY;

    var username = pkbConnectorProperties.getUsername();
    var password = pkbConnectorProperties.getPassword();

    if (StringUtils.isNotBlank(username)) {
      requestEntity = new HttpEntity(kdnRequest, authHeaders(username, password));
    } else {
      requestEntity = new HttpEntity<>(kdnRequest);
    }

    log.info("HTTP PUT {} payload: {}", url, kdnRequest);
    var response = restTemplate.exchange(
        url,
        HttpMethod.PUT,
        requestEntity,
        ApplicationReport.class
    );

    if (response.getStatusCode().is2xxSuccessful()) {
      ApplicationReport report = response.getBody();
      if (Objects.nonNull(report.getErrorCode()) && "4".equals(report.getErrorCode())) {
        log.info("PKB kdn response {}", report.getErrorMessage());
        throw new KdnReportFailedException("Failed to get KDN score");
      }
      if (Objects.nonNull(report)) {
        return report;
      }
    }
    throw new KdnReportFailedException("Failed to get KDN score");
  }

  @Override
  public PkbReportsDto getAllPkbReports(PkbReportsRequest request) {
    var url = pkbConnectorProperties.getUrl() + "/credit-report/all";
    var requestEntity = HttpEntity.EMPTY;

    var username = pkbConnectorProperties.getUsername();
    var password = pkbConnectorProperties.getPassword();

    if (StringUtils.isNotBlank(username)) {
      requestEntity = new HttpEntity(request, authHeaders(username, password));
    } else {
      requestEntity = new HttpEntity<>(request);
    }

    try {
      var response = restTemplate
          .postForEntity(url, requestEntity, PkbReportsDto.class);
      if (response.getStatusCode().is2xxSuccessful() && Objects.nonNull(response.getBody())) {
        return response.getBody();
      }
    } catch (Exception e) {
      log.error("Failed to get pkb reports from pkb-connector");
      throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");
    }
    log.error("Failed to get pkb reports from pkb-connector");
    throw new PkbConnectorReportsFailedException("Pkb-Connector report request failed");
  }

  private HttpHeaders authHeaders(String username, String password) {
    return new HttpHeaders() {
      {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = "Basic " + new String(encodedAuth);
        set("Authorization", authHeader);
      }
    };
  }
}
