package kz.codesmith.epay.loan.api.service;

import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;

public interface ICoreClientService {

  UserDto getUserById(Long id);

  ClientDto getClientByClientName(String clientName);

}
