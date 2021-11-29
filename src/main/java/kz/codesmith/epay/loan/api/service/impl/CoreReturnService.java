package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.model.cashout.CoreInitResponseDto;
import kz.codesmith.epay.loan.api.model.core.CorePaymentReturnDto;
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
public class CoreReturnService {

  private final RestTemplate restTemplate;
  private final MfsCoreProperties coreProperties;

  public CoreInitResponseDto initReturnPayment(CorePaymentReturnDto dto) {
    String url = coreProperties.getUrl() + coreProperties.getReturnPaymentUrl();

    HttpHeaders headers = new HttpHeaders();
    headers.setBasicAuth(coreProperties.getAgentName(), coreProperties.getAgentPassword());

    HttpEntity<CorePaymentReturnDto> httpEntity = new HttpEntity<>(dto, headers);
    ResponseEntity<CoreInitResponseDto> response = restTemplate.exchange(url, HttpMethod.POST,
        httpEntity, CoreInitResponseDto.class);
    return response.getBody();
  }
}
