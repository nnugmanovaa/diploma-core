package kz.codesmith.epay.loan.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import kz.codesmith.logger.request.XrequestId;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {

  public static final String PAYMENT_SEND_ROUTING_KEY = "payment.loan-payments";
  public static final String PAYMENT_EXCHANGE_NAME = "loan-payments-exchange";
  public static final String PAYMENT_QUEUE_NAME = "queue-loan-payments";
  public static final String PAYMENT_ROUTING_KEY_TEMPLATE = "#.loan-payments";

  private final ObjectMapper objectMapper;
  private final XrequestId xrequestid;

  @Autowired
  public AmqpConfig(
      ObjectMapper objectMapper,
      XrequestId xrequestid
  ) {
    this.objectMapper = objectMapper;
    this.xrequestid = xrequestid;
  }

  @Bean
  public TopicExchange paymentTopicExchange() {
    return new TopicExchange(PAYMENT_EXCHANGE_NAME);
  }

  @Bean
  public Queue paymentQueue() {
    return new Queue(PAYMENT_QUEUE_NAME);
  }

  @Bean
  public Binding bindingPaymentQueue() {
    return BindingBuilder
        .bind(paymentQueue())
        .to(paymentTopicExchange())
        .with(PAYMENT_ROUTING_KEY_TEMPLATE);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  /**
   * RabbitTemplate configuration bean.
   *
   * @param connectionFactory {@link ConnectionFactory}
   * @return {@link RabbitTemplate}
   */
  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate();
    rabbitTemplate.setMessageConverter(jsonMessageConverter());
    rabbitTemplate.setConnectionFactory(connectionFactory);
    rabbitTemplate.setChannelTransacted(true);
    rabbitTemplate.setBeforePublishPostProcessors(message -> {
      message.getMessageProperties().setHeader(XrequestId.REQUEST_ID_PARAM, xrequestid.get());
      message.getMessageProperties()
          .setHeader(XrequestId.REQUEST_ID_USER, xrequestid.getUsername());
      return message;
    });
    return rabbitTemplate;
  }

  /**
   * SimpleRabbitListenerContainerFactory configuration bean.
   *
   * @param connectionFactory {@link ConnectionFactory}
   * @param configurer        {@link SimpleRabbitListenerContainerFactoryConfigurer}
   * @return {@link SimpleRabbitListenerContainerFactory}
   */
  @Bean
  public SimpleRabbitListenerContainerFactory jsaFactory(
      ConnectionFactory connectionFactory,
      SimpleRabbitListenerContainerFactoryConfigurer configurer
  ) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(jsonMessageConverter());
    factory.setAdviceChain(
        (MethodInterceptor) invocation -> {
          Message message = (Message) invocation.getArguments()[1];

          xrequestid.set(Optional.ofNullable(message.getMessageProperties()
              .getHeaders()
              .get(XrequestId.REQUEST_ID_PARAM))
              .map(Object::toString)
              .orElseGet(xrequestid::get));
          xrequestid.setUsername(Optional.ofNullable(message.getMessageProperties()
              .getHeaders()
              .get(XrequestId.REQUEST_ID_USER))
              .map(Object::toString)
              .orElse("none"));

          return invocation.proceed();
        });
    return factory;
  }

  @Bean
  @ConditionalOnMissingClass("org.springframework.orm.jpa.JpaTransactionManager")
  public RabbitTransactionManager rabbitTransactionManager(
      org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
    return new RabbitTransactionManager(connectionFactory);
  }
}
