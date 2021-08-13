package kz.codesmith.epay.loan.api.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {
  @Value("${s3-storage.endpoint-url}")
  private String endpointUrl;
  @Value("${s3-storage.access-key}")
  private String accessKey;
  @Value("${s3-storage.secret-key}")
  private String secretKey;

  @Bean
  public AWSCredentials getCredentials() {
    return new BasicAWSCredentials(accessKey, secretKey);
  }

  @Bean
  public AmazonS3 getS3Client() {
    var conn = new AmazonS3Client(getCredentials());
    conn.setEndpoint(endpointUrl);
    return conn;
  }
}
