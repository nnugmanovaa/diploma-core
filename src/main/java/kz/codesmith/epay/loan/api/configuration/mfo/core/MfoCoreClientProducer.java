package kz.codesmith.epay.loan.api.configuration.mfo.core;

import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.soap.WsStubs;
import kz.payintech.siteexchange.SiteExchange;
import kz.payintech.siteexchange.SiteExchangePortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Lazy
@Configuration
public class MfoCoreClientProducer extends AbstractClientProducer {

  private final MfoCoreProperties mfoCoreProperties;

  private final WsStubs wsStubs;

  @Autowired
  MfoCoreClientProducer(
      MfoCoreProperties mfoCoreProperties,
      WsStubs wsStubs
  ) {
    this.mfoCoreProperties = mfoCoreProperties;
    this.wsStubs = wsStubs;
  }

  @Bean
  protected SiteExchangePortType mfoCoreWebService() throws Exception {
    SiteExchangePortType proxy = wsStubs
        .createServiceProxy(SiteExchangePortType.class, SiteExchange.class);
    setupUrls(proxy, mfoCoreProperties.getUrl() + "/SiteExchange");
    setupLogging(proxy);
    setupAuthUsingDefaultUser(
        proxy,
        mfoCoreProperties.getUsername(),
        mfoCoreProperties.getPassword()
    );
    setTimeout(proxy, mfoCoreProperties.getServiceTimeout());
    return setupJavaProxies(SiteExchangePortType.class, proxy);
  }

}

