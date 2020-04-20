package com.example.connector.config;

import java.net.http.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

  @Bean
  public HttpClient oAuth2HttpClient() {
    return HttpClient.newBuilder().build();
  }

}
