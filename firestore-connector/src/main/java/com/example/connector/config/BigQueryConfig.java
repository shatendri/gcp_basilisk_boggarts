package com.example.connector.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.common.util.concurrent.ListenableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.bigquery.core.BigQueryTemplate;
import org.springframework.cloud.gcp.bigquery.integration.BigQuerySpringMessageHeaders;
import org.springframework.cloud.gcp.bigquery.integration.outbound.BigQueryFileMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class BigQueryConfig {

    @Value("${firestore.service-account-key-path}")
    private String serviceAccountKeyPath;

    @Value("${bigquery.dataset}")
    private String dataset;

    @Profile("default")
    @Bean
    public BigQuery bigQuery() {
        return BigQueryOptions.getDefaultInstance().getService();
    }

    @Profile("dev")
    @Bean
    public BigQuery bigQueryWithCreds() throws IOException {
        return BigQueryOptions.newBuilder()
                .setCredentials(
                        GoogleCredentials.fromStream(
                                new FileInputStream(new File(serviceAccountKeyPath))
                        )
                )
                .build()
                .getService();
    }

    @Bean
    public BigQueryTemplate bigQueryTemplate(BigQuery bigQuery) {
        return new BigQueryTemplate(bigQuery, dataset);
    }

}
