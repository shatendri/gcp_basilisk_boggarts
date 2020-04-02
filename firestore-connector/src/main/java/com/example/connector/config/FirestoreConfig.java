package com.example.connector.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirestoreConfig {

  @Value("${firestore.service-account-key-path}")
  private String serviceAccountKeyPath;

  @Bean
  public Firestore firestore() {
    return FirestoreOptions.getDefaultInstance().getService();
  }

}
