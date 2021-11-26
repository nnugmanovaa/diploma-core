package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.model.cashout.CashoutInitResponseDto;
import kz.codesmith.epay.loan.api.model.cashout.InitClientWalletTopUpDirectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreTopUpService {

  public static final String CORE_TOPUP_OPERATION_NAME = "topup";

  private final RestTemplate restTemplate;
  private final MfsCoreProperties coreProperties;

  public CashoutInitResponseDto initClientTopUpDirect(InitClientWalletTopUpDirectDto dto) {
    String url = coreProperties.getUrl() + coreProperties.getInitTopUpUrl();

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(coreProperties.getAgentName(), coreProperties.getAgentPassword());

    HttpEntity<InitClientWalletTopUpDirectDto> httpEntity = new HttpEntity<>(dto, headers);
    ResponseEntity<CashoutInitResponseDto> response = restTemplate.exchange(url, HttpMethod.POST,
        httpEntity, CashoutInitResponseDto.class);
    return response.getBody();
  }
}
