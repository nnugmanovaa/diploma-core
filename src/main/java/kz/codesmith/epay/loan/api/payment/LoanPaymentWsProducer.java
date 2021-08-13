package kz.codesmith.epay.loan.api.payment;

import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.soap.WsStubs;
import kz.pitech.mfo.PaymentServices;
import kz.pitech.mfo.PaymentServicesPortType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Slf4j
@Lazy
@Configuration
public class LoanPaymentWsProducer extends AbstractClientProducer {
  private final WsStubs wsStubs;
  private final MfoAppProperties mfoAppProperties;

  @Autowired
  LoanPaymentWsProducer(MfoAppProperties mfoAppProperties, WsStubs wsStubs) {
    this.mfoAppProperties = mfoAppProperties;
    this.wsStubs = wsStubs;
  }

  @Bean
  protected PaymentServicesPortType payMfoWebService() throws Exception {
    PaymentServicesPortType proxy = wsStubs
        .createServiceProxy(PaymentServicesPortType.class, PaymentServices.class);
    setupUrls(proxy, mfoAppProperties.getUrl());

    setupLogging(proxy);
    setTimeout(proxy, mfoAppProperties.getReadTimeout());
    setupAuthUsingDefaultUser(proxy, mfoAppProperties.getUsername(),
        mfoAppProperties.getPassword());

    return setupJavaProxies(PaymentServicesPortType.class, proxy);
  }
}
