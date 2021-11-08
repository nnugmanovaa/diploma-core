package kz.codesmith.epay.loan.api.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Locale;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import kz.codesmith.epay.core.shared.converter.json.LocalDateSimpleDeserializer;
import kz.codesmith.epay.core.shared.converter.json.LocalDateSimpleSerializer;
import kz.codesmith.epay.core.shared.converter.json.LocalDateTimeFromZonedDeserializer;
import kz.codesmith.epay.core.shared.converter.json.LocalDateTimeToZonedSerializer;
import kz.codesmith.epay.core.shared.converter.json.ZonedDateTimeSimpleDeserializer;
import kz.codesmith.epay.core.shared.converter.json.ZonedDateTimeSimpleSerializer;
import kz.codesmith.epay.loan.api.configuration.mfs.core.MfsCoreProperties;
import kz.codesmith.epay.loan.api.domain.PkbCheckEntity;
import kz.codesmith.epay.loan.api.model.map.LoanMapper;
import kz.codesmith.epay.loan.api.model.pkb.Check;
import kz.codesmith.epay.loan.api.model.pkb.DynamicCheck;
import kz.codesmith.epay.loan.api.util.LocalDateTimeShortSerializer;
import lombok.RequiredArgsConstructor;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mapstruct.factory.Mappers;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.xml.sax.SAXException;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

  private final MfsCoreProperties mfsCoreProperties;

  @Bean
  public LoanMapper loanMapper() {
    return Mappers.getMapper(LoanMapper.class);
  }

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

    createCheck2PkbCheckEntity(modelMapper);
    createDynamicCheck2Check(modelMapper);

    return modelMapper;
  }

  private void createDynamicCheck2Check(ModelMapper mapper) {
    mapper.createTypeMap(DynamicCheck.class, Check.class)
        .addMapping(DynamicCheck::getCode, Check::setCode)
        .addMapping(DynamicCheck::getTitle, Check::setTitle)
        .addMapping(src -> src.getStatus().getId(), (d, v) -> d.getStatus().setId((String) v))
        .addMapping(DynamicCheck::getUrl, ((d, v) -> d.getSource().setValue((String) v)))
        .addMapping(
            DynamicCheck::getDate, ((d, v) -> d.getActualDate().setValue((LocalDateTime) v)));
  }

  private void createCheck2PkbCheckEntity(ModelMapper mapper) {
    mapper.createTypeMap(Check.class, PkbCheckEntity.class)
        .addMapping(Check::getCode, PkbCheckEntity::setCode)
        .addMapping(Check::getTitle, PkbCheckEntity::setTitle)
        .addMapping(src -> src.getStatus().getId(), PkbCheckEntity::setStatus)
        .addMapping(src -> src.getSource().getValue(), PkbCheckEntity::setSource)
        .addMapping(src -> src.getSearchBy().getValue(), PkbCheckEntity::setSearchBy)
        .addMapping(src -> src.getActualDate().getValue(), PkbCheckEntity::setActualDate)
        .addMapping(src -> src.getRefreshDate().getValue(), PkbCheckEntity::setRefreshDate);
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder)
      throws NoSuchAlgorithmException, KeyManagementException {
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }

          public void checkClientTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }

          public void checkServerTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }
        }
    };
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
    CloseableHttpClient httpClient = HttpClients.custom()
        .setSSLContext(sslContext)
        .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        .build();
    HttpComponentsClientHttpRequestFactory customRequestFactory =
        new HttpComponentsClientHttpRequestFactory();
    customRequestFactory.setReadTimeout(mfsCoreProperties.getServiceTimeout().intValue());
    customRequestFactory
        .setConnectionRequestTimeout(mfsCoreProperties.getServiceTimeout().intValue());
    customRequestFactory.setConnectTimeout(mfsCoreProperties.getServiceTimeout().intValue());
    customRequestFactory.setHttpClient(httpClient);
    return builder.requestFactory(() -> customRequestFactory).build();
  }

  @Bean
  public Module jsonMapperJava8DateTimeModule() {
    var bean = new SimpleModule();

    bean.addDeserializer(ZonedDateTime.class, new ZonedDateTimeSimpleDeserializer());
    bean.addSerializer(ZonedDateTime.class, new ZonedDateTimeSimpleSerializer());

    bean.addDeserializer(LocalDateTime.class, new LocalDateTimeFromZonedDeserializer());
    bean.addSerializer(LocalDateTime.class, new LocalDateTimeShortSerializer());

    bean.addDeserializer(LocalDate.class, new LocalDateSimpleDeserializer());
    bean.addSerializer(LocalDate.class, new LocalDateSimpleSerializer());

    return bean;
  }

  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(jsonMapperJava8DateTimeModule());

    return mapper;
  }

  @Bean
  public LocaleResolver localeResolver() {
    AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
    resolver.setDefaultLocale(new Locale("ru"));
    resolver.setSupportedLocales(Arrays.asList(
        new Locale("ru"),
        new Locale("kk"),
        new Locale("en")
    ));
    return resolver;
  }

  @Bean
  public MessageSource messageSource() {
    var messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

  @Bean
  public SAXParser getSaxParser() throws ParserConfigurationException, SAXException {
    SAXParserFactory factory = SAXParserFactory.newInstance();
    return factory.newSAXParser();
  }
}
