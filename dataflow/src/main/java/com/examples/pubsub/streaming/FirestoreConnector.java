package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.UserDto;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.vendor.grpc.v1p21p0.com.google.gson.Gson;
import org.apache.beam.vendor.grpc.v1p21p0.com.google.gson.JsonSyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class FirestoreConnector extends DoFn<String, String> {

    private final static Logger LOG = LoggerFactory.getLogger(JsonToUserDto.class);

    private final String filePath;

    public FirestoreConnector(String filePath) {
        this.filePath = filePath;
    }

    @ProcessElement
    public void processElement(ProcessContext c) throws IOException {
        Firestore firestore = FirestoreOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(filePath))
                        .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform")))
                .build()
                .getService();
        String entityJson = c.element();
        Gson gson = new Gson();

        UserDto userDto = new UserDto();

        try {
            userDto = gson.fromJson(entityJson, UserDto.class);
        } catch (JsonSyntaxException e) {
            LOG.error("Cast json to UserDto was failed:" + e.getMessage());
            e.printStackTrace();
        }

        try {
            CollectionReference usersCollectionReference = firestore.collection("users8");
            usersCollectionReference.add(userDto);
        } catch (Exception e) {
            LOG.error("Failed to save user to Firestore");
            LOG.error(e.getMessage());
        }
    }
}
