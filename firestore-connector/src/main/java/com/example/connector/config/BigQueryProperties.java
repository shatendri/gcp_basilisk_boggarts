package com.example.connector.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "bigquery")
@Configuration
@Getter
@Setter
public class BigQueryProperties {
    private String table;
    private String dataSet;
}
