package com.example.connector.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirestoreConfig {

    @Value("${firestore.service-account-key-path}")
    private String serviceAccountKeyPath;

    @Profile("default")
    @Bean
    public Firestore firestore() {
        return FirestoreOptions.getDefaultInstance().getService();
    }

    @Profile("dev")
    @Bean
    public Firestore firestoreWithCreds() throws IOException {
        return FirestoreOptions.getDefaultInstance().newBuilder()
                .setProjectId("gcp-trainings-272313")
                .setCredentials(
                        GoogleCredentials.fromStream(
                                new FileInputStream(new File(serviceAccountKeyPath))
                        )
                )
                .build()
                .getService();
    }

}
