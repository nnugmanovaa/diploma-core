package kz.codesmith.epay.loan.api.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import kz.codesmith.epay.core.shared.converter.Utils;
import kz.codesmith.epay.core.shared.model.OwnerType;
import kz.codesmith.epay.core.shared.model.SimpleStatus;
import kz.codesmith.epay.core.shared.model.clients.AgentValidationRecord;
import kz.codesmith.epay.core.shared.model.clients.ClientCreateDto;
import kz.codesmith.epay.core.shared.model.clients.ClientDto;
import kz.codesmith.epay.core.shared.model.clients.ClientIdentificationStatus;
import kz.codesmith.epay.core.shared.model.clients.ClientInfoDto;
import kz.codesmith.epay.core.shared.model.clients.PasswordRecoveryDto;
import kz.codesmith.epay.core.shared.model.exceptions.AlreadyExistApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.BlackListedApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.ClientException;
import kz.codesmith.epay.core.shared.model.exceptions.ClientNotValidatedApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.IllegalApiUsageGeneralApiException;
import kz.codesmith.epay.core.shared.model.exceptions.NotActiveApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.OtpStatusException;
import kz.codesmith.epay.core.shared.model.exceptions.accounts.ClientNotFoundException;
import kz.codesmith.epay.core.shared.model.otp.OtpClientValidationDto;
import kz.codesmith.epay.core.shared.model.users.UserDto;
import kz.codesmith.epay.core.shared.model.users.UserRole;
import kz.codesmith.epay.core.shared.model.users.UserUpdateDto;
import kz.codesmith.epay.core.shared.service.IMessageService;
import kz.codesmith.epay.loan.api.domain.clients.ClientEntity;
import kz.codesmith.epay.loan.api.domain.clients.ValidateData;
import kz.codesmith.epay.loan.api.model.ProcessConstants;
import kz.codesmith.epay.loan.api.model.orders.otp.OtpCheckStatus;
import kz.codesmith.epay.loan.api.model.otp.OtpCheckClientStatusDto;
import kz.codesmith.epay.loan.api.model.otp.OtpClientDto;
import kz.codesmith.epay.loan.api.repository.ClientsRepository;
import kz.codesmith.epay.loan.api.service.IClientsServices;
import kz.codesmith.epay.loan.api.service.IUsersCachedService;
import kz.codesmith.epay.security.component.JwtTokenUtil;
import kz.codesmith.epay.security.domain.UserEntity;
import kz.codesmith.epay.security.model.EpayCoreUser;
import kz.codesmith.epay.security.model.UserContext;
import kz.codesmith.epay.security.model.UserContextHolder;
import kz.codesmith.epay.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClientsServicesImpl implements IClientsServices {

  public static final String OTP_CANNOT_BE_SEND_FOR = "OTP cannot be send for: ";
  public static final String ATTEMPTS_EXCEEDED_MESSAGE = "Number of OTP attempts was exceeded";
  private final ClientsRepository clientsRepository;
  private final ModelMapper simpleMapper;
  private final UserContextHolder userContext;
  private final IUsersCachedService usersService;
  private final UserRepository userRepository;
  private final JwtTokenUtil jwtTokenUtil;
  private final Environment env;

  @Override
  @Transactional
  public ClientDto getOrCreateClient(String abonentCode) {
    Optional<ClientDto> clientDto = clientsRepository
        .findFirstByClientName(abonentCode)
        .map(e -> simpleMapper.map(e, ClientDto.class));
    return clientDto.get();
  }

  @Override
  public ClientDto getClientChecked(String abonentCode) {
    ClientDto dto = clientsRepository
        .findFirstByClientName(abonentCode)
        .map(e -> simpleMapper.map(e, ClientDto.class))
        .orElseThrow(() -> new NotFoundApiServerException(ApiErrorTypeParamValues.CLIENT,
            abonentCode));

    if (!dto.getStatus().equals(SimpleStatus.ACTIVE)) {
      throw new NotActiveApiServerException(ApiErrorTypeParamValues.CLIENT, dto.getClientsId());
    }

    return dto;
  }

  @Override
  public ClientDto getClientByIinAndClientName(String iin, String clientName) {
    return clientsRepository.findByIinAndClientName(iin, clientName)
        .map(e -> simpleMapper.map(e, ClientDto.class))
        .orElse(null);
  }

  @Override
  public void checkClientValidationForAgent(ClientDto client, Integer agentsId) {
    if (client.getValidateData() == null || client.getValidateData().stream()
        .noneMatch(v -> v.getAgentsId().equals(agentsId))) {
      throw new NotFoundApiServerException(ApiErrorTypeParamValues.CLIENT,
          client.getClientName(), "For Agent " + agentsId);
    }

    if (client.getValidateData().stream()
        .noneMatch(v -> v.getAgentsId().equals(agentsId) && v.isUsernameValid())) {
      throw new ClientNotValidatedApiServerException(client.getClientName(), agentsId);
    }
  }

  @Override
  public ClientDto getClient(String abonentCode) {
    return clientsRepository
        .findFirstByClientName(abonentCode)
        .map(e -> simpleMapper.map(e, ClientDto.class))
        .orElse(null);

  }

  @Override
  @Transactional(noRollbackFor = {ClientException.class, OtpStatusException.class})
  public OtpCheckClientStatusDto validateClient(OtpClientDto otpClientDto, Integer agentsId) {
    UserDto user = usersService.getByUsername(otpClientDto.getClientName());
    if (user == null) {
      throw new ClientNotFoundException(otpClientDto.getClientName());
    }

    Optional<UserEntity> userEntity = userRepository.findByUsername(user.getUsername());

    OtpCheckClientStatusDto statusDto = OtpCheckClientStatusDto.builder().build();

    if (userEntity.isEmpty()) {
      throw new EntityNotFoundException();
    }

    if (user.getOwnerType().equals(OwnerType.CLIENT)) {
      ClientEntity entity = clientsRepository.findById(user.getOwnerId())
          .orElseThrow(() -> new NotFoundApiServerException(ApiErrorTypeParamValues.CLIENT,
              user.getOwnerId()));

      if (!entity.getStatus().equals(SimpleStatus.ACTIVE)) {
        throw new NotActiveApiServerException(ApiErrorTypeParamValues.CLIENT, user.getOwnerId());
      }

      if (entity.getValidateData() == null) {
        entity.setValidateData(ValidateData.builder().build());
      }

      Optional<AgentValidationRecord> validationRecord = entity.getValidateData().getValidations()
          .stream().filter(v -> v.getAgentsId().equals(agentsId)).findFirst();

      int userValidationAttempts = env.getProperty(
          ProcessConstants.USER_VALIDATION_ATTEMPTS_PROPERTY,
          Integer.TYPE
      );

      if (validationRecord.isPresent()) {
        if (validationRecord.get().getOtpDto().getOtpAttemptCounter() > userValidationAttempts) {
          entity.setStatusBy(userContext.getContext().getUsername());
          entity.setStatusTime(Utils.now());
          entity.setStatusComment(ATTEMPTS_EXCEEDED_MESSAGE);
          entity.setStatus(SimpleStatus.SUSPENDED);
          throw new ClientException(ApiErrorType.E500_USER_IS_BLOCKED);
        }
        if (validationRecord.get().getOtpDto().isOnceChecked()) {
          throw new ClientException(ApiErrorType.E500_ALREADY_VALIDATED);
        } else {
          if (!validationRecord.get().getAgentsId().equals(agentsId)) {
            throw new UnsupportedOperationException();
          }

          if (validationRecord.get().getOtpDto().getCode().equals(otpClientDto.getOtpCode())) {
            validationRecord.get().getOtpDto().setOnceChecked(true);
            statusDto.setStatus(OtpCheckStatus.OK);
            validationRecord.get().setUsernameValid(true);
            userEntity.get().setStatus(SimpleStatus.ACTIVE);
          } else {
            validationRecord.get().getOtpDto().setOtpAttemptCounter(
                validationRecord.get().getOtpDto().getOtpAttemptCounter() + 1);
            throw new OtpStatusException(ApiErrorType.E500_OTP_MISMATCH);
          }
        }
      }
    } else {
      throw new IllegalApiUsageGeneralApiException();
    }
    return statusDto;
  }


  @Override
  public Optional<ClientDto> getClientById(Integer id) {
    Optional<ClientDto> clientDto = clientsRepository
        .findById(id)
        .map(e -> simpleMapper.map(e, ClientDto.class));
    return clientDto;
  }



  @Override
  public void checkClientIsNotBlocked(ClientDto client) {
    if (client.getStatus().equals(SimpleStatus.SUSPENDED)) {
      throw new NotActiveApiServerException(ApiErrorTypeParamValues.CLIENT, client.getClientsId());
    } else if (client.getStatus().equals(SimpleStatus.BLACK)) {
      throw new BlackListedApiServerException(ApiErrorTypeParamValues.CLIENT,
          client.getClientsId());
    }
  }

  @Override
  public Page<ClientDto> getAllClients(Pageable pageable) {
    return clientsRepository.findAll(pageable).map(e -> simpleMapper.map(e, ClientDto.class));
  }

  @Override
  public Page<ClientDto> getClientsByStatus(SimpleStatus status, Pageable pageable) {
    return clientsRepository.findAllByStatus(status, pageable)
        .map(e -> simpleMapper.map(e, ClientDto.class));
  }


  @Override
  public String generateToken(OtpClientDto otpClientDto) {
    UserDto userDto = usersService.getByUsername(otpClientDto.getClientName());

    List<SimpleGrantedAuthority> authorities = userDto.getRoles().stream()
        .map(r -> new SimpleGrantedAuthority(r.name()))
        .collect(Collectors.toList());

    var userDetails = new EpayCoreUser(
        userDto.getUsername(),
        userDto.getPassword(),
        userDto.getStatus().equals(SimpleStatus.ACTIVE),
        true,
        true,
        true,
        authorities,
        userDto.getUserId(),
        userDto.getOwnerType(),
        userDto.getOwnerId(),
        userDto.getOperatorsId(),
        userDto.getOwnerName()
    );
    return jwtTokenUtil.generateToken(userDetails);
  }

  private OtpCheckClientStatusDto constructErrorResponse(String message, UserDto userDto) {
    return OtpCheckClientStatusDto
        .builder()
        .status(OtpCheckStatus.ERROR)
        .message(message)
        .build();
  }

  private int generateCode(long currentTimeMillis) {
    Random random = new Random(currentTimeMillis);
    return random.nextInt(10000);
  }

  @Transactional
  public void saveNewPassword(
      String clientName,
      final String newPassword,
      Integer agentId,
      boolean isPasswordEncoded,
      boolean skipChangeableStatusCheck
  ) {

    UserDto userDto = usersService.getByUsername(clientName);

    if (userDto != null) {
      ClientEntity clientEntity = clientsRepository.findFirstByClientName(userDto.getUsername())
          .orElseThrow(() -> new NotFoundApiServerException(ApiErrorTypeParamValues.CLIENT,
              userDto.getUsername()));

      Optional<AgentValidationRecord> validationRecord = clientEntity.getValidateData()
          .getValidations()
          .stream().filter(v -> v.getAgentsId().equals(agentId)).findFirst();

      if (!skipChangeableStatusCheck
          && !validationRecord.get().getPasswordRecoveryDto().isPasswordChangeableStatus()) {
        throw new UnsupportedOperationException();
      }

      UserUpdateDto userUpdateDto = simpleMapper.map(userDto, UserUpdateDto.class);
      userUpdateDto.setPasswordEncoded(isPasswordEncoded);

      userUpdateDto.setPassword(newPassword);
      validationRecord.get().getPasswordRecoveryDto().setPasswordChangeableStatus(false);
      if (!validationRecord.get().getOtpDto().isOnceChecked()) {
        userUpdateDto.setStatus(SimpleStatus.ACTIVE);
        validationRecord.get().getOtpDto().setOtpAttemptCounter(0);
        validationRecord.get().getOtpDto().setOnceChecked(true);
      }

      usersService.updateUser(userUpdateDto);
      clientsRepository.save(clientEntity);

    } else {
      throw new UsernameNotFoundException(clientName);
    }
  }

  @Override
  public boolean isUserBlocked(String username) {
    Optional<ClientEntity> client = clientsRepository.findFirstByClientName(username);

    return !Objects.equals(client.get().getStatus(), SimpleStatus.ACTIVE);
  }

  @Override
  public Optional<ClientDto> getClientByClientName(String clientName) {
    Optional<ClientDto> clientDto = clientsRepository.findFirstByClientName(clientName)
        .map(e -> simpleMapper.map(e, ClientDto.class));
    return clientDto;
  }

  @Override
  public ClientDto createNewClientWithAccountStructure(ClientCreateDto dto, boolean isValidated) {
    var userContext = this.userContext.getContext();

    Integer agentsId = 1000;

    ClientEntity clientEntity = new ClientEntity();
      clientEntity = simpleMapper.map(dto, ClientEntity.class);
      clientEntity.setInsertedBy("root");
      clientEntity.setUpdatedBy(clientEntity.getInsertedBy());
      clientEntity.setStatus(SimpleStatus.ACTIVE);
      clientEntity.setIdentificationStatus(ClientIdentificationStatus.UNIDENTIFIED);
      clientEntity.setStatusTime(LocalDateTime.now());
      clientEntity.setValidateData(ValidateData.builder().build());

      clientEntity.setStatusBy("root");

      clientEntity.getValidateData().getValidations().add(AgentValidationRecord.builder()
          .agentsId(agentsId)
          .isUsernameValid(isValidated)
          .otpDto(new OtpClientValidationDto())
          .passwordRecoveryDto(PasswordRecoveryDto.builder().build())
          .build());

      clientEntity = clientsRepository.save(clientEntity);

      usersService.createDefaultClientUser(
          dto.getPassword(),
          clientEntity.getClientsId(),
          clientEntity.getClientName(),
          isValidated,
          UserRole.CLIENT_USER
      );

      return simpleMapper.map(clientEntity, ClientDto.class);
  }

  private Integer getAgentsIdFromValidationData(ClientDto client) {
    AgentValidationRecord agentValidationRecord = client
        .getValidateData()
        .stream()
        .filter(agentRecord -> agentRecord.getAgentsId() != null)
        .findFirst()
        .orElseThrow(() -> new GeneralApiServerException(ApiErrorType.E500_CLIENT_NOT_VALIDATED));
    return agentValidationRecord.getAgentsId();
  }
}
