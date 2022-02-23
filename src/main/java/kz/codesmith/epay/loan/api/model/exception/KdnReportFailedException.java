package kz.codesmith.epay.loan.api.model.exception;

import java.util.HashMap;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorParam;
import kz.codesmith.epay.core.shared.model.exceptions.ApiErrorType;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;

public class KdnReportFailedException extends GeneralApiServerException {
  public KdnReportFailedException() {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR);
  }

  public KdnReportFailedException(String message) {
    super(ApiErrorType.E500_INTERNAL_SERVER_ERROR, new HashMap<>() {
      {
        put(ApiErrorParam.MESSAGE, message);
      }
    });
  }
}
