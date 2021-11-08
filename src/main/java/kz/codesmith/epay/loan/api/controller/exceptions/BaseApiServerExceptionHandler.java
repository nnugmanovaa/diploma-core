package kz.codesmith.epay.loan.api.controller.exceptions;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import kz.codesmith.epay.core.shared.model.errors.ApiError;
import kz.codesmith.epay.core.shared.model.errors.SimpleApiError;
import kz.codesmith.epay.core.shared.model.exceptions.GeneralApiServerException;
import kz.codesmith.epay.core.shared.model.exceptions.NotFoundApiServerException;
import kz.codesmith.epay.loan.api.exception.TooEarlyForRequestException;
import kz.codesmith.epay.loan.api.exception.VerificationCodeExpiredException;
import kz.codesmith.epay.loan.api.exception.VerificationException;
import kz.codesmith.epay.loan.api.exception.VerificationRequestNotFoundException;
import kz.codesmith.epay.loan.api.exception.WrongVerificationCodeException;
import kz.codesmith.epay.loan.api.model.exception.MfoGeneralApiException;
import kz.codesmith.logger.request.XrequestId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ControllerAdvice
public class BaseApiServerExceptionHandler extends ResponseEntityExceptionHandler {

  private static final ObjectMapper generalApiServerExceptionMapper = new ObjectMapper();
  private final XrequestId requestId;

  /**
   * Main constructor. Initializes objectMaper for serialization of GeneralApiServerException to
   * Json
   */
  @Autowired
  public BaseApiServerExceptionHandler(XrequestId requestId) {
    generalApiServerExceptionMapper
        .setVisibility(generalApiServerExceptionMapper.getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
            .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withSetterVisibility(JsonAutoDetect.Visibility.NONE));
    this.requestId = requestId;
  }


  /**
   * {@link GeneralApiServerException} handler.
   *
   * @param ex      {@link GeneralApiServerException}
   * @param request {@link WebRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(value = {MfoGeneralApiException.class})
  public ResponseEntity<Object> handleGeneralApiServerError(GeneralApiServerException ex,
      WebRequest request) {

    String message = "Произошла ошибка, попробуйте еще раз";

    final List<Object> parameters = new ArrayList<>();

    parameters.add(ex.getErrorParameters());

    ApiError apiError = ApiError.builder()
        .requestId(requestId.get())
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .code(ex.getErrorType().name())
        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(message)
        .parameters(parameters)
        .build();

    return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
  }

  /**
   * {@link MethodArgumentTypeMismatchException} handler.
   *
   * @param ex      {@link MethodArgumentTypeMismatchException}
   * @param request {@link WebRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex,
      WebRequest request) {

    ApiError apiError = ApiError.builder()
        .requestId(requestId.get())
        .code(ex.getErrorCode())
        .message(ex.getLocalizedMessage())
        .httpStatus(HttpStatus.BAD_REQUEST.value())
        .status(HttpStatus.BAD_REQUEST)
        .build();

    return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
  }

  /**
   * {@link NotFoundApiServerException} handler.
   *
   * @param ex      {@link NotFoundApiServerException}
   * @param request {@link WebRequest}
   * @return {@link ResponseEntity}
   */
  @ExceptionHandler(value = {NotFoundApiServerException.class})
  public ResponseEntity<Object> handleNotFoundApiServer(NotFoundApiServerException ex,
      WebRequest request) {

    ApiError apiError = ApiError.builder()
        .requestId(requestId.get())
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .code(ex.getErrorType().name())
        .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message(ex.getLocalizedMessage())
        .build();

    return handleExceptionInternal(ex, apiError, new HttpHeaders(), apiError.getStatus(), request);
  }

  @ExceptionHandler(VerificationException.class)
  public ResponseEntity<ApiError> handleVerificationErrors(
      VerificationException exc) {
    var errorResponse = ApiError.builder()
        .requestId(requestId.get())
        .status(exc.getStatus())
        .code(exc.getErrorCode())
        .message(exc.getMessage())
        .build();
    return new ResponseEntity<>(errorResponse, exc.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {

    String message = ex.getBindingResult().getAllErrors()
        .stream()
        .findFirst()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .orElseGet(ex::getMessage);

    final SimpleApiError apiError = SimpleApiError.builder()
        .message(message)
        .build();

    return handleExceptionInternal(ex, apiError, headers, status, request);
  }

}
