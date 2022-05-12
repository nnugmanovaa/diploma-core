package kz.codesmith.epay.loan.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import kz.codesmith.logger.request.XrequestId;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

@Configuration
public class AmqpConfig {

  public static final String LOAN_STATUSES_ROUTING_KEY = "loan.loan-status";
  public static final String LOAN_STATUSES_IIN_ROUTING_KEY = "loan.loan-status-by-iin";
  public static final String LOAN_STATUSES_EXCHANGE_NAME = "loan-status-exchange";
  public static final String LOAN_STATUSES_QUEUE_NAME = "queue-loan-status";
  public static final String LOAN_STATUSES_IIN_QUEUE_NAME = "queue-loan-iin-status";
  public static final String LOAN_STATUSES_ROUTING_KEY_TEMPLATE = "#.loan-status";
  public static final String LOAN_STATUSES_IIN_ROUTING_KEY_TEMPLATE = "#.loan-status-by-iin";


  public static final String LOAN_WAIT_EXCHANGE_NAME = "loan-wait-exchange";
  public static final String LOAN_WAIT_QUEUE_NAME = "queue-loan-wait";
  public static final String DLX_EXCHANGE_NAME = "loan-dlx-exchange";
  public static final String DLX_QUEUE_NAME = "queue-loan-dlx";
  public static final String LOAN_STATUS_DLX_QUEUE_NAME = "queue-loan-status-dlx";
  public static final String LOAN_STATUS_DLX_EXCHANGE_NAME = "loan-status-dlx-exchange";

  @Value("${loan-status-delay}")
  private int loanStatusDelay;

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
  public FanoutExchange waitExchange() {
    return new FanoutExchange(LOAN_WAIT_EXCHANGE_NAME);
  }

  @Bean
  public FanoutExchange dlxExchange() {
    return new FanoutExchange(DLX_EXCHANGE_NAME);
  }

  @Bean
  public FanoutExchange loanStatusDlxExchange() {
    return new FanoutExchange(LOAN_STATUS_DLX_EXCHANGE_NAME);
  }

  @Bean
  public Queue waitQueue() {
    return QueueBuilder.durable(LOAN_WAIT_QUEUE_NAME)
        .ttl(loanStatusDelay)
        .deadLetterExchange(LOAN_STATUS_DLX_EXCHANGE_NAME)
        .build();
  }

  @Bean
  public Queue dlxQueue() {
    return QueueBuilder.durable(DLX_QUEUE_NAME)
        .build();
  }

  @Bean
  public Queue loanStatusDlxQueue() {
    return QueueBuilder.durable(LOAN_STATUS_DLX_QUEUE_NAME)
        .build();
  }

  @Bean
  public Binding bindingDlxQueue() {
    return BindingBuilder
        .bind(dlxQueue())
        .to(dlxExchange());
  }

  @Bean
  public Binding bindingLoanStatusDlxQueue() {
    return BindingBuilder
        .bind(loanStatusDlxQueue())
        .to(loanStatusDlxExchange());
  }

  @Bean
  public Binding bindingWaitQueue() {
    return BindingBuilder
        .bind(waitQueue())
        .to(waitExchange());
  }

  @Bean
  public TopicExchange loanStatusTopicExchange() {
    return new TopicExchange(LOAN_STATUSES_EXCHANGE_NAME);
  }

  @Bean
  public Queue loanStatusQueue() {
    return QueueBuilder.durable(LOAN_STATUSES_QUEUE_NAME)
        .build();
  }

  @Bean
  public Queue loanIinStatusQueue() {
    return QueueBuilder.durable(LOAN_STATUSES_IIN_QUEUE_NAME)
        .build();
  }

  @Bean
  public Binding bindingLoanStatusQueue() {
    return BindingBuilder
        .bind(loanStatusQueue())
        .to(loanStatusTopicExchange())
        .with(LOAN_STATUSES_ROUTING_KEY_TEMPLATE);
  }

  @Bean
  public Binding bindingLoanIinStatusQueue() {
    return BindingBuilder
        .bind(loanIinStatusQueue())
        .to(loanStatusTopicExchange())
        .with(LOAN_STATUSES_IIN_ROUTING_KEY_TEMPLATE);
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
