package kz.codesmith.epay.loan.api.configuration;

import com.google.common.collect.Lists;
import java.time.LocalTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

  private static final String AUTHORIZATION_HEADER = "Authorization";

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Loan")
        .select()
        .apis(RequestHandlerSelectors
            .basePackage("kz.codesmith.epay.loan.api.controller"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo())
        .forCodeGeneration(true)
        .securityContexts(Lists.newArrayList(jwtAuthContext()))
        .securitySchemes(Lists.newArrayList(jwtAuthScheme()))
        .directModelSubstitute(LocalTime.class, String.class);
  }

  @Bean
  public Docket apiMfoLoans() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("1c")
        .select()
        .apis(RequestHandlerSelectors
            .basePackage("kz.codesmith.epay.loan.api.payment"))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo())
        .forCodeGeneration(true)
        .securityContexts(Lists.newArrayList(jwtAuthContext()))
        .securitySchemes(Lists.newArrayList(jwtAuthScheme()))
        .directModelSubstitute(LocalTime.class, String.class);
  }

  private SecurityContext jwtAuthContext() {
    return SecurityContext.builder()
        .securityReferences(Lists.newArrayList(jwtAuthReference()))
        .build();
  }

  private SecurityReference jwtAuthReference() {
    final AuthorizationScope[] authScopes = {};
    return new SecurityReference(jwtAuthScheme().getName(), authScopes);
  }

  private ApiKey jwtAuthScheme() {
    return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder().title("PiTech Loan API")
        .description("The REST API for PiTech Loan").termsOfServiceUrl("")
        .contact(new Contact("Fintech Inc.", "", "info@finteh.llc"))
        .license("Apache License Version 2.0")
        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
        .version("1.0")
        .build();
  }
}
