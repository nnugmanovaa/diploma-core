package kz.codesmith.epay.loan.api.model.exception;

import java.util.HashMap;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorParam;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;

public class ScoringUnreachableException extends GeneralApiServerException {

  public ScoringUnreachableException() {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  public ScoringUnreachableException(String message) {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR, new HashMap<>() {
      {
        put(ApiErrorParam.MESSAGE, message);
      }
    });
  }

}
