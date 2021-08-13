package kz.codesmith.epay.loan.api.configuration.pkb;

import com.creditinfo.ws.kdn2.KdnService;
import com.creditinfo.ws.kdn2.KdnService_Service;
import com.creditinfo.ws.score.CigWsHeader;
import com.creditinfo.ws.score.ScoreService;
import com.creditinfo.ws.score.ScoreService_Service;
import com.fcb.closedcontracts.service.web.Service;
import com.fcb.closedcontracts.service.web.Service_Service;
import kz.codesmith.epay.loan.api.component.PkbCheckRs;
import kz.codesmith.epay.loan.api.util.PkbBehaviorAuthInterceptor;
import kz.codesmith.epay.ws.connector.configuration.AbstractClientProducer;
import kz.codesmith.epay.ws.connector.configuration.rest.RestStubs;
import kz.codesmith.epay.ws.connector.configuration.soap.WsStubs;
import kz.com.fcb.fico.FicoScoringService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PkbClientProducer extends AbstractClientProducer {

  private final RestStubs restStubs;
  private final PkbProperties pkbProperties;
  private final WsStubs wsStubs;

  @Bean
  protected PkbCheckRs pkbCheckRs() {
    PkbCheckRs proxy = restStubs.createServiceProxy(
        pkbProperties.getUrl() + "/asource/v1/",
        pkbProperties.getUsername(),
        pkbProperties.getPassword(),
        pkbProperties.getServiceTimeout(),
        pkbProperties.getServiceTimeout(),
        PkbCheckRs.class
    );
    return setupJavaProxies(PkbCheckRs.class, proxy);
  }

  @SneakyThrows
  @Bean
  protected ScoreService pkbBehaviorScoreService() {
    ScoreService proxy = wsStubs.createServiceProxy(ScoreService.class, ScoreService_Service.class);
    setupUrls(proxy, pkbProperties.getUrl() + "/ScoreService/ScoreService");
    setupLogging(proxy);
    var cigWsHeader = new CigWsHeader();
    cigWsHeader.setCulture("RU");
    cigWsHeader.setPassword(pkbProperties.getPassword());
    cigWsHeader.setUserName(pkbProperties.getUsername());
    cigWsHeader.setSecurityToken("0");
    cigWsHeader.setUserId(0);
    cigWsHeader.setVersion("1");
    setupAuth(proxy, cigWsHeader);
    setTimeout(proxy, pkbProperties.getServiceTimeout());
    return setupJavaProxies(ScoreService.class, proxy);
  }

  @SneakyThrows
  @Bean
  protected kz.com.fcb.fico.ScoreService pkbFicoScoreService() {
    kz.com.fcb.fico.ScoreService proxy = wsStubs.createServiceProxy(
        kz.com.fcb.fico.ScoreService.class,
        FicoScoringService.class
    );
    setupUrls(proxy, pkbProperties.getUrl() + "/FicoWs/FicoScoringService");
    setupLogging(proxy);
    setTimeout(proxy, pkbProperties.getServiceTimeout());
    return setupJavaProxies(kz.com.fcb.fico.ScoreService.class, proxy);
  }

  @SneakyThrows
  @Bean
  protected KdnService pkbKdnService() {
    KdnService proxy = wsStubs.createServiceProxy(KdnService.class, KdnService_Service.class);
    setupUrls(proxy, pkbProperties.getUrl() + "/kdn/kdnService");
    setupLogging(proxy);
    setTimeout(proxy, pkbProperties.getServiceTimeout());
    return setupJavaProxies(KdnService.class, proxy);
  }

  @SneakyThrows
  @Bean
  protected Service pkbClosedContractsService() {
    Service proxy = wsStubs.createServiceProxy(Service.class, Service_Service.class);
    setupUrls(proxy, pkbProperties.getUrl() + "/ClosedContracts/Service");
    setupLogging(proxy);
    setTimeout(proxy, pkbProperties.getServiceTimeout());
    return setupJavaProxies(Service.class, proxy);
  }

  protected void setupAuth(Object proxy, CigWsHeader cigWsHeader) {
    Client client = ClientProxy.getClient(proxy);
    client.getOutInterceptors().add(new PkbBehaviorAuthInterceptor(() -> cigWsHeader));
  }

}
