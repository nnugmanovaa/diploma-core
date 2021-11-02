package kz.codesmith.epay.loan.api.service.impl;

import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import kz.codesmith.epay.loan.api.component.coid.CoidConnector;
import kz.codesmith.epay.loan.api.component.coid.IdentityMatchResponse;
import kz.codesmith.epay.loan.api.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityMatchService {
  private final CoidConnector coidConnector;
  private final RetryTemplate retryTemplate;

  @SneakyThrows
  public IdentityMatchResponse matchUserIdentity(
      String iin, String requestId, String base64FaceImage) {
    try {
      var timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
      var tempFileName = "face_" + iin + "_" + timestamp;
      var tempFilePath = FileUtil.createTempFileWithTimeStamp("/faces", tempFileName, "jpg");
      var faceImageFile = FileUtil.base64MimeStringToImageFile(base64FaceImage, tempFilePath);

      var identityMatch = coidConnector.matchUserIdentityByPhoto(iin, faceImageFile, requestId);

      log.info("Identity Match requestId={} iin={} with result={}",
          requestId, iin, identityMatch.getResult());

      return identityMatch;
    } catch (SocketTimeoutException e) {
      log.info("trying to get matching result for iin={}, idempotencyKey={}", iin, requestId);
      return retryTemplate
          .execute(context -> coidConnector
              .getMatchingResult(iin, requestId));
    } catch (Exception e) {
      log.error("Can't match user. requestId={} iin={}. {}", requestId, iin, e.getMessage());
    }
    return null;
  }
}
