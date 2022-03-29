package kz.codesmith.epay.loan.api.controller;

import java.util.Optional;
import javax.validation.Valid;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.ClientNotValidatedApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.accounts.UserIsBlockedApiServerException;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.loan.api.model.security.JwtResponse;
import kz.codesmith.epay.loan.api.model.security.LoginRequest;
import kz.codesmith.epay.loan.api.service.IClientsServices;
import kz.codesmith.epay.loan.api.service.IUsersCachedService;
import kz.codesmith.epay.security.component.JwtTokenUtil;
import kz.codesmith.epay.security.component.ListenedDaoAuthenticationProvider;
import kz.codesmith.epay.security.model.EpayCoreUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/auth")
public class AuthController {

  private final ListenedDaoAuthenticationProvider authenticationProvider;
  private final JwtTokenUtil jwtTokenUtil;
  private final IUsersCachedService usersCachedService;
  private final IClientsServices clientsServices;

  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

    UserDto userDto = usersCachedService.getByUsername(loginRequest.getUsername());

    if (userDto == null) {
      throw new GeneralApiServerException(ApiErrorType.E500_USER_NOT_FOUND);
    }

    Authentication authentication = authenticationProvider.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(), loginRequest.getPassword()));

    var userDetails = (EpayCoreUser) authentication.getPrincipal();
    String jwt = jwtTokenUtil.generateToken(userDetails);

    JwtResponse response = JwtResponse.builder()
        .id(userDetails.getUserId())
        .accessToken(jwt)
        .username(userDetails.getUsername())
        .roles(userDetails.getUserAuthorities())
        .build();

    if (userDetails.getUserAuthorities().contains("CLIENT_USER")) {
      Optional<ClientDto> clientDto = clientsServices
          .getClientByClientName(userDetails.getUsername());
      clientDto.ifPresent(client -> {
        response
            .setIdentificationStatus(client.getIdentificationStatus());
        response.setClientId(client.getClientsId());
        response.setFirstName(client.getFirstName());
        response.setLastName(client.getLastName());
        response.setMiddleName(client.getMiddleName());
        response.setIin(client.getIin());
      });
    }

    return ResponseEntity.ok(response);
  }
}
