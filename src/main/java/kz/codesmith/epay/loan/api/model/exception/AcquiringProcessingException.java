package kz.codesmith.epay.loan.api.model.exception;

import java.util.HashMap;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorParam;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;

public class AcquiringProcessingException extends GeneralApiServerException {
  public AcquiringProcessingException() {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  public AcquiringProcessingException(String message) {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR, new HashMap<>() {
      {
        put(ApiErrorParam.MESSAGE, message);
      }
    });
  }
}

