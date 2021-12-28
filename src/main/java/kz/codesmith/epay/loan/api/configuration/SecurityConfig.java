package kz.codesmith.epay.loan.api.configuration;

import java.util.Arrays;
import java.util.Collections;
import kz.codesmith.epay.security.component.JwtAuthenticationEntryPoint;
import kz.codesmith.epay.security.component.JwtAuthenticationFilter;
import kz.codesmith.epay.security.component.JwtAuthorizationFilter;
import kz.codesmith.epay.security.component.JwtTokenUtil;
import kz.codesmith.epay.security.configuration.BaseSecurityConfiguration;
import kz.codesmith.logger.request.XrequestId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.CachingUserDetailsService;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@Order(102)
class SecurityConfig extends BaseSecurityConfiguration {

  @Configuration
  @Order(1)
  public static class ApiSystemSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.cors().and().csrf().disable()
          .antMatcher("/system/**")
          .authorizeRequests()
          .antMatchers(HttpMethod.GET, "/actuator/**").permitAll()
          .antMatchers(
              HttpMethod.GET,
              "/v2/api-docs",
              "/swagger-resources/**",
              "/swagger-ui.html",
              "/webjars/**"
          ).permitAll()
          .and()
          .httpBasic().realmName("PiTech API")
          .and()
          .headers().frameOptions().disable();
    }
  }

  @Configuration
  @Order(3)
  @RequiredArgsConstructor
  public static class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtTokenUtil jwtTokenUtil;
    private final XrequestId xrequestId;
    private final CachingUserDetailsService userDetailsService;

    private final String[] permittedUrls = {
        "/actuator/**",
        "/swagger-resources/**", "/swagger-ui.html",
        "/webjars/**", "/v2/api-docs",
        "/",
        "/csrf"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.csrf().disable().cors(Customizer.withDefaults())
          .authorizeRequests()
          .antMatchers(HttpMethod.GET, permittedUrls).permitAll()
          .antMatchers(HttpMethod.POST, "/halyk/callback").permitAll()
          .antMatchers(HttpMethod.POST, "/acquiring/callback").permitAll()
          .antMatchers(HttpMethod.POST, "/public/loan/schedule/calculation").permitAll()
          .anyRequest().authenticated()
          .and()
          .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
          .and()
          .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtTokenUtil))
          .addFilter(new JwtAuthorizationFilter(
              authenticationManager(),
              jwtTokenUtil,
              userDetailsService,
              xrequestId
          ))
          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .headers().frameOptions().disable();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      return cors -> {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(
            Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(
            Arrays.asList("authorization", "content-type", "x-auth-token"));
        configuration.setExposedHeaders(Collections.singletonList("x-auth-token"));
        configuration.setAllowCredentials(true);
        return configuration;
      };
    }

  }

  @Configuration
  @Order(2)
  @RequiredArgsConstructor
  public static class SwaggerSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      http.cors().and().csrf().disable()
          .requestMatchers()
          .antMatchers(
              "/ws/**"
          ).and()
          .authorizeRequests()
          .anyRequest()
          .hasAnyAuthority("AGENT_USER", "MERCHANT_USER")
          .and()
          .httpBasic();
    }
  }
}
