package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.service.ICoreClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreClientService implements ICoreClientService {

  private final MfsCoreProperties coreProperties;
  private final CoreAuthService coreAuthService;
  private final RestTemplate restTemplate;

  @Override
  public UserDto getUserById(Long id) {
    var url = coreProperties.getUrl() + coreProperties.getGetUserUrl() + "/" + id;
    var httpEntity = new HttpEntity(getHeadersWithAccessToken(getAuthToken()));
    var resp = restTemplate.exchange(
        url, HttpMethod.GET, httpEntity, UserDto.class
    );
    return resp.getBody();
  }

  @Override
  public ClientDto getClientByClientName(String clientName) {
    var url = coreProperties.getUrl() + coreProperties.getGetClientUrl() + "/" + clientName;
    var httpEntity = new HttpEntity(getHeadersWithAccessToken(getAuthToken()));
    var resp = restTemplate.exchange(
        url, HttpMethod.GET, httpEntity, ClientDto.class
    );
    return resp.getBody();
  }

  private HttpHeaders getHeadersWithAccessToken(String accessToken) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    return headers;
  }

  private String getAuthToken() {
    return coreAuthService.signIn(
        coreProperties.getUsername(), coreProperties.getPassword()).getAccessToken();
  }
}
