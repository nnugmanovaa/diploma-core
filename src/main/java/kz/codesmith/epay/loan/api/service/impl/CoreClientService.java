package kz.codesmith.epay.loan.api.service.impl;

import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.clients.ClientWithContactDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.model.ClientEditDto;
import kz.codesmith.epay.loan.api.service.ICoreClientService;
import kz.codesmith.epay.loan.api.service.IPaymentService;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreClientService implements ICoreClientService {

  private final MfsCoreProperties coreProperties;
  private final CoreAuthService coreAuthService;
  private final RestTemplate restTemplate;
  private final UserContextHolder userContextHolder;

  @Override
  public UserDto getUserById(Long id) {
    var url = coreProperties.getUrl() + coreProperties.getGetUserUrl() + "/" + id;
    var httpEntity = new HttpEntity(getHeadersWithAccessToken(
        getAuthToken(coreProperties.getUsername(), coreProperties.getPassword())
    ));
    var resp = restTemplate.exchange(
        url, HttpMethod.GET, httpEntity, UserDto.class
    );
    return resp.getBody();
  }

  @Override
  public ClientDto getClientByClientName(String clientName) {
    var url = coreProperties.getUrl() + coreProperties.getGetClientUrl() + "/"
        + clientName;
    var httpEntity = new HttpEntity(getHeadersWithAccessToken(
        getAuthToken(coreProperties.getUsername(), coreProperties.getPassword())));
    var resp = restTemplate.exchange(
        url, HttpMethod.GET, httpEntity, ClientDto.class
    );
    return resp.getBody();
  }

  @Override
  public String uploadAvatar(MultipartFile multipartFile) {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
    requestHeaders.setBearerAuth(getAuthToken(coreProperties.getAgentTopUsername(),
        coreProperties.getAgentTopPassword()));
    HttpHeaders requestHeadersAttachment = new HttpHeaders();
    requestHeadersAttachment.setContentType(MediaType.IMAGE_PNG);
    HttpEntity<ByteArrayResource> attachmentPart;
    try {
      ByteArrayResource fileAsResource = new ByteArrayResource(multipartFile.getBytes()) {
        @Override
        public String getFilename() {
          return multipartFile.getOriginalFilename();
        }
      };
      attachmentPart = new HttpEntity<>(fileAsResource,requestHeadersAttachment);
    } catch (Exception e) {
      return null;
    }
    MultiValueMap<String, Object> multipartRequest = new LinkedMultiValueMap<>();
    multipartRequest.set("file",attachmentPart);

    var headers = new HttpHeaders();
    headers.setBearerAuth(getAuthToken(coreProperties.getAgentTopUsername(),
        coreProperties.getAgentTopPassword()));
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Integer> requestEntity =
        new HttpEntity<Integer>(userContextHolder.getContext().getOwnerId(), headers);
    multipartRequest.add("clientId", requestEntity);

    HttpEntity<MultiValueMap<String,Object>> request =
        new HttpEntity<>(multipartRequest,requestHeaders);

    var url = coreProperties.getUrl() + coreProperties.getClientsProfileUrl() + "/upload";
    var resp = restTemplate.exchange(
        url, HttpMethod.POST, request, String.class
    );
    return resp.getBody();
  }

  @Override
  public ClientWithContactDto updateClientProfile(ClientEditDto dto) {
    dto.setClientName(userContextHolder.getContext().getUsername());
    var url = coreProperties.getUrl() + coreProperties.getClientsProfileUrl();
    var httpEntity = new HttpEntity<>(dto, getHeadersWithAccessToken(
        getAuthToken(coreProperties.getAgentTopUsername(),
            coreProperties.getAgentTopPassword())));
    var resp = restTemplate.exchange(
        url, HttpMethod.PUT, httpEntity, ClientWithContactDto.class
    );
    return resp.getBody();
  }

  private HttpHeaders getHeadersWithAccessToken(String accessToken) {
    var headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    return headers;
  }

  private String getAuthToken(String username, String password) {
    return coreAuthService.signIn(
        username, password).getAccessToken();
  }
}
