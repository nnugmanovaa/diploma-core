package kz.codesmith.epay.loan.api.configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import java.util.HashMap;
import java.util.Map;
import kz.codesmith.epay.loan.api.configuration.pkb.PkbProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@RequiredArgsConstructor
public class RedisCacheConfig {

  public static final String PKB_CACHE_NAME = "pkb-cache";
  public static final String PKB_STOP_FACTOR_CACHE_NAME = "pkb-stop-factor";
  private final PkbProperties pkbProperties;

  @Bean
  RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
    return (builder) -> {
      Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>();
      configurationMap.put(PKB_CACHE_NAME,
          RedisCacheConfiguration.defaultCacheConfig()
              .serializeValuesWith(RedisSerializationContext.SerializationPair
                  .fromSerializer(jackson2jsonRedisSerializer()))
              .entryTtl(pkbProperties.getCacheDurationTtl()));

      configurationMap.put(
          PKB_STOP_FACTOR_CACHE_NAME,
          RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(pkbProperties.getCacheDurationTtl())
      );
      builder.withInitialCacheConfigurations(configurationMap);
    };
  }

  public Jackson2JsonRedisSerializer jackson2jsonRedisSerializer() {
    var objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.enableDefaultTyping(DefaultTyping.NON_FINAL);
    Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
    serializer.setObjectMapper(objectMapper);
    return serializer;
  }

}
