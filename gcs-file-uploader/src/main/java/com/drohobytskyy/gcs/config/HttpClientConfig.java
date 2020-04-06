package com.drohobytskyy.gcs.config;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    public static final int CONNECTION_TIMEOUT_MILLIS = 10000;

    @Bean
    public HttpClient mockarooHttpClient() {
        return
          HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .connectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT_MILLIS))
            .build();
    }
}
