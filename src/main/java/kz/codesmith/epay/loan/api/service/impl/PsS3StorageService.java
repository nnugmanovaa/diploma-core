package kz.codesmith.epay.loan.api.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import kz.codesmith.epay.loan.api.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PsS3StorageService implements StorageService {

  private final AmazonS3 s3client;

  @Value("${s3-storage.bucket-name}")
  private String bucketName;

  public void put(String key, InputStream input, String mediaType) {
    log.info("Put to {} file {}", bucketName, key);
    var metadata = new ObjectMetadata();
    metadata.setContentType(mediaType);
    s3client.putObject(bucketName, key, input, metadata);
  }

  public InputStream get(String key) {
    log.info("Get from {} file {}", bucketName, key);
    return s3client.getObject(
        bucketName,
        key
    ).getObjectContent();
  }
}
