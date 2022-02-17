package kz.codesmith.epay.loan.api.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kz.codesmith.epay.loan.api.component.PkbCheckRs;
import kz.codesmith.epay.loan.api.domain.PkbCheckEntity;
import kz.codesmith.epay.loan.api.domain.PkbCheckManagementEntity;
import kz.codesmith.epay.loan.api.model.CheckResult;
import kz.codesmith.epay.loan.api.model.CheckStatus;
import kz.codesmith.epay.loan.api.model.pkb.Check;
import kz.codesmith.epay.loan.api.model.pkb.PkbResponse;
import kz.codesmith.epay.loan.api.repository.PkbChecksManagementRepository;
import kz.codesmith.epay.loan.api.repository.PkbChecksResultHistoryRepository;
import kz.codesmith.epay.loan.api.service.ILoanChecksService;
import kz.codesmith.epay.loan.api.service.IPkbScoreService;
import kz.codesmith.epay.ws.connector.utils.JaxBUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoanChecksService implements ILoanChecksService {

  private final PkbCheckRs pkbCheckRs;
  private final JaxBUtils jaxBUtils;
  private final PkbChecksManagementRepository pkbChecksManagementRepository;
  private final ModelMapper modelMapper;
  private final PkbChecksResultHistoryRepository pkbChecksResultHistoryRepository;
  private final IPkbScoreService pkbScoreService;

  @Value("${integration.pkb.check-result-actual-days}")
  private int checkResultActualDays;

  @SneakyThrows
  @Override
  public CheckResult stopFactorCheck(String iin) {
    List<String> errors = new ArrayList<>();
    List<PkbCheckManagementEntity> activeChecks =
        pkbChecksManagementRepository.getAllByIsActive(true);

    CheckResult localCheck = stopFactorLocalCheck(iin);

    if (!localCheck.getErrors().isEmpty()) {
      return new CheckResult(errors);
    }

    String respString = pkbScoreService.getCustomerInfoByIin(iin);
    PkbResponse resp = jaxBUtils.unmarshall(respString, PkbResponse.class);

    savePkbResponseToDB(resp);

    List<Check> checksList = getChecksListFromResponse(resp);
    checksList.stream()
        .filter(Objects::nonNull)
        .filter(i ->
            i.getStatus() != null
                && i.getStatus().getId() != null
                && Objects.equals(i.getStatus().getId(), CheckStatus.FOUND.getStatus())
        ).forEach(i ->
        activeChecks.forEach(r -> {
          if (Objects.equals(i.getCode(), r.getCode())) {
            errors.add(r.getRejectionText());
          }
        }));

    return new CheckResult(errors);

  }

  @Override
  public CheckResult stopFactorLocalCheck(String iin) {
    List<String> errors = new ArrayList<>();

    List<PkbCheckEntity> actualChecks = pkbChecksResultHistoryRepository
        .getAllByIinAndRequestDateAfterAndStatus(
            iin,
            LocalDateTime.now().minusDays(checkResultActualDays),
            CheckStatus.FOUND.getStatus());

    List<PkbCheckManagementEntity> activeChecks =
        pkbChecksManagementRepository.getAllByIsActive(true);
    actualChecks.forEach(i ->
        activeChecks.forEach(r -> {
          if (Objects.equals(i.getCode(), r.getCode())) {
            errors.add(r.getRejectionText());
          }
        }));

    return new CheckResult(errors);
  }

  @Override
  public String stopFactorCheckRaw(String iin) {
    return pkbScoreService.getCustomerInfoByIin(iin);
  }

  private List<Check> getChecksListFromResponse(PkbResponse resp) {
    List<Check> checksList = Arrays.stream(resp.getClass().getDeclaredFields())
        .filter(field -> field.getType().isAssignableFrom(Check.class))
        .map(field -> {
          try {
            field.setAccessible(true);
            return (Check) field.get(resp);
          } catch (IllegalAccessException e) {
            log.error("error", e);
          }
          throw new ClassCastException();
        })
        .collect(Collectors.toList());

    checksList.addAll(resp.getDynamicChecks().stream()
        .map(i -> modelMapper.map(i, Check.class)).collect(Collectors.toList()));

    return checksList;
  }

  private void savePkbResponseToDB(PkbResponse resp) {
    List<Check> checksList = getChecksListFromResponse(resp);
    log.debug("LoanChecksService.savePkbResponseToDB(); checksList = {}", checksList);
    List<PkbCheckEntity> pkbCheckEntities = checksList.stream()
        .filter(Objects::nonNull)
        .map(i -> {
          try {
            PkbCheckEntity entity = modelMapper.map(i, PkbCheckEntity.class);
            entity.setIin(resp.getCustomerMainInfo().getIinBin().getValue());
            entity.setRequestDate(resp.getCustomerMainInfo().getRequestDate().getValue());
            return entity;
          } catch (IllegalArgumentException e) {
            log.warn("IllegalArgumentException: source cannot be null. "
                + "Can't map Check {} to PkbCheckEntity", i);
            return null;
          }
        }).filter(x -> x != null).collect(Collectors.toList());
    pkbChecksResultHistoryRepository.saveAll(pkbCheckEntities);
  }

}
