package com.example.splitter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
public class WebSecurityConfiguration {


  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public SecurityFilterChain createBeautifulSecurityFilterChain(HttpSecurity sec) throws Exception {
    sec.authorizeHttpRequests(configurer -> {
          configurer.requestMatchers("/", "/css/*", "/api/*/*/*", "/api/*", "/api/*/*").permitAll()
              .anyRequest().authenticated();
        })
        .csrf()
        .ignoringRequestMatchers("/api/*/*/*", "/api/*", "/api/*/*")
        .and()
        .oauth2Login(
        );
    return sec.build();
  }


}
