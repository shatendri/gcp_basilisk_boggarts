package com.example.connector.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class Configuration {

    @Value("${firestore.service-account-key.path}")
    private String keyPath;

    @Profile("default")
    @Bean
    public Firestore firestore() {
        return FirestoreOptions.newBuilder()
                .build()
                .getService();
    }

    @Profile("dev")
    @Bean
    public Firestore firestoreWithCreds() throws IOException {
        return FirestoreOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(
                        new FileInputStream(
                                new File(keyPath))))
                .build()
                .getService();
    }
}
