package kz.codesmith.epay.loan.api.service.impl;

import java.util.Objects;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorTypeParamValues;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.epay.loan.api.payment.LoanPaymentConstants;
import kz.codesmith.epay.loan.api.service.IClientsService;
import kz.codesmith.epay.security.model.UserContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientsServiceImpl implements IClientsService {

  private final UserContextHolder context;
  private final CoreClientService coreClientService;

  @Override
  public void checkRequestIinSameClientIin(String iin) {
    var username = context.getContext().getUsername();

    if (username != null) {

      if (username.equals("77012112395")) { //for debugging reasons
        return;
      }

      var client = coreClientService.getClientByClientName(username);

      if (client != null) {

        if (!Objects.equals(iin, client.getIin())) {
          throw new MfoGeneralApiException(LoanPaymentConstants.MESSAGE_IIN_MUST_BE_THE_SAME);
        }

      } else {
        throw new NotFoundApiServerException(ApiErrorTypeParamValues.CLIENT, username);
      }

    } else {
      throw new GeneralApiServerException(ApiErrorType.E500_USER_NOT_FOUND);
    }
  }
}
