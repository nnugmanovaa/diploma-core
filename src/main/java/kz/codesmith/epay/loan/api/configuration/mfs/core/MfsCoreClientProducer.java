package kz.codesmith.epay.loan.api.configuration.mfs.core;

import kz.codesmith.epay.loan.api.component.IAgentClientTransferRs;
import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.rest.RestStubs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MfsCoreClientProducer extends AbstractClientProducer {

  private final MfsCoreProperties mfsCoreProperties;
  private final RestStubs restStubs;

  @Bean
  protected IAgentClientTransferRs mfsAgentClientTransferRs() {
    IAgentClientTransferRs proxy = restStubs.createServiceProxy(
        mfsCoreProperties.getUrl(),
        mfsCoreProperties.getUsername(),
        mfsCoreProperties.getPassword(),
        mfsCoreProperties.getServiceTimeout(),
        mfsCoreProperties.getServiceTimeout(),
        IAgentClientTransferRs.class
    );
    return setupJavaProxies(IAgentClientTransferRs.class, proxy);
  }

}

