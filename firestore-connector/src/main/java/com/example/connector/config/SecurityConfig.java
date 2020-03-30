package com.example.connector.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    return
        http
            .authorizeExchange()

            .pathMatchers(HttpMethod.GET, "/users")
            .hasAnyAuthority("ROLE_ADMIN", "ROLE_EDITOR", "ROLE_READER")

            .pathMatchers(HttpMethod.PUT, "/users")
            .hasAnyAuthority("ROLE_ADMIN", "ROLE_EDITOR")

            .pathMatchers(HttpMethod.POST, "/users")
            .hasAnyAuthority("ROLE_ADMIN")

            .pathMatchers("/authorization-url")
            .permitAll()

            .anyExchange().authenticated()
            .and()
            .oauth2ResourceServer()
            .jwt().and().and().build();
  }
}
