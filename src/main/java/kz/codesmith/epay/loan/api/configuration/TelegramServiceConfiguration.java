package kz.codesmith.epay.loan.api.configuration;

import kz.codesmith.epay.telegram.gw.controller.ITelegramBotController;
import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.rest.RestStubs;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TelegramServiceProperties.class)
@RequiredArgsConstructor
public class TelegramServiceConfiguration extends AbstractClientProducer {

  private final RestStubs restStubs;
  private final TelegramServiceProperties properties;

  @Bean
  public ITelegramBotController telegramApi() {

    ITelegramBotController proxy = restStubs.createServiceProxy(
        properties.getUrl(),
        properties.getUsername(),
        properties.getPassword(),
        properties.getServiceTimeout(),
        properties.getServiceTimeout(),
        ITelegramBotController.class
    );
    return setupJavaProxies(ITelegramBotController.class, proxy);
  }
}
