package kz.codesmith.epay.loan.api.payment.ws;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
  @Bean
  public ServletRegistrationBean<MessageDispatcherServlet>
        messageDispatcherServlet(ApplicationContext applicationContext) {
    MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setApplicationContext(applicationContext);
    servlet.setTransformWsdlLocations(true);
    return new ServletRegistrationBean<>(servlet, "/ws/*");
  }

  @Bean(name = "paymentServices")
  public Wsdl11Definition defaultWsdl11Definition(XsdSchema paymentSchema) {
    CustomWsdl11Definition wsdl11Definition = new CustomWsdl11Definition();
    wsdl11Definition.setPortTypeName("PaymentServicesPortType");
    wsdl11Definition.setLocationUri("/ws");
    wsdl11Definition.setTargetNamespace("http://www.integracia.kz");
    wsdl11Definition.setSchema(paymentSchema);
    wsdl11Definition.setRequestSuffix("");
    wsdl11Definition.setServiceName("PaymentServices");
    return wsdl11Definition;
  }

  @Bean
  public XsdSchema paymentSchema() {
    return new SimpleXsdSchema(new ClassPathResource("wsdl/PitechPayment.xsd"));
  }
}
