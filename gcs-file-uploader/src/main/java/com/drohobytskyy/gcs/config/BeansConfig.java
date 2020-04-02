package com.drohobytskyy.gcs.config;

import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BeansConfig {

    @Bean
    public HttpClient configServiceHttpClient() {
        log.info("Building HttpClient.");
        return
          HttpClient.newBuilder()
            .version(Version.HTTP_1_1)
            .connectTimeout(Duration.ofMillis(10000))
            .build();
    }
}
