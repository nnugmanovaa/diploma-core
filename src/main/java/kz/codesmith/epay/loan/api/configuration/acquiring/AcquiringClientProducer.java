package kz.codesmith.epay.loan.api.configuration.acquiring;

import kz.codesmith.epay.loan.api.component.acquiring.AcquiringRs;
import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.rest.RestStubs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AcquiringClientProducer extends AbstractClientProducer {

  private final AcquiringProperties acquiringProperties;
  private final RestStubs restStubs;

  @Bean
  protected AcquiringRs acquiringRs() {
    AcquiringRs proxy = restStubs.createServiceProxy(
        acquiringProperties.getUrl(),
        acquiringProperties.getUsername(),
        acquiringProperties.getPassword(),
        acquiringProperties.getReadTimeout(),
        acquiringProperties.getConnectTimeout(),
        AcquiringRs.class,
        null);
    return setupJavaProxies(AcquiringRs.class, proxy);
  }
}