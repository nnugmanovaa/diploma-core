package kz.codesmith.epay.loan.api.service;

import java.util.Optional;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientCreateDto;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.clients.ClientIdentificationStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientInfoDto;
import kz.codesmith.epay.loan.api.model.otp.OtpCheckClientStatusDto;
import kz.codesmith.epay.loan.api.model.otp.OtpClientDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClientsServices {

  ClientDto getOrCreateClient(String abonentCode);

  ClientDto getClientChecked(String abonentCode);

  ClientDto getClientByIinAndClientName(String iin, String clientName);

  Optional<ClientDto> getClientById(Integer id);

  void checkClientValidationForAgent(ClientDto client, Integer agentsId);

  ClientDto getClient(String abonentCode);

  OtpCheckClientStatusDto validateClient(OtpClientDto otpClientDto, Integer agentsId);

  void checkClientIsNotBlocked(ClientDto client);

  Page<ClientDto> getAllClients(Pageable pageable);

  Page<ClientDto> getClientsByStatus(SimpleStatus status, Pageable pageable);

  String generateToken(OtpClientDto otpClientDto);

  void saveNewPassword(
      String clientName,
      final String newPassword,
      Integer agentId,
      boolean isPasswordEncoded,
      boolean skipChangeableStatusCheck
  );

  boolean isUserBlocked(String username);

  Optional<ClientDto> getClientByClientName(String clientName);

  ClientDto createNewClientWithAccountStructure(ClientCreateDto dto, boolean isValidated);
}
