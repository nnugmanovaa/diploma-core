package kz.codesmith.epay.loan.api.configuration;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.util.XRLog;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentGenerationConfig {

  @Bean
  public PebbleEngine getTemplateEngine() {
    XRLog.setLoggerImpl(new Slf4jLogger());
    return new PebbleEngine.Builder()
        .newLineTrimming(false)
        .build();
  }
}
