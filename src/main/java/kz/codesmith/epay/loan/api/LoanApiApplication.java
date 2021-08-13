package kz.codesmith.epay.loan.api;

import co.elastic.apm.attach.ElasticApmAttacher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling
@SpringBootApplication(
    scanBasePackages = {"kz.codesmith.epay", "kz.codesmith.logger"}
)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableCaching
public class LoanApiApplication {

  public static void main(String[] args) {
    ElasticApmAttacher.attach();
    SpringApplication.run(LoanApiApplication.class, args);
  }

}
