package com.drohobytskyy.gcs.service;

import com.drohobytskyy.gcs.domain.MockarooParamsContainer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class MockarooReader {

    HttpClient httpClient;
    MockarooParamsContainer mockarooParamsContainer;

    @Autowired
    public MockarooReader(HttpClient httpClient,
      MockarooParamsContainer mockarooParamsContainer) {
        this.httpClient = httpClient;
        this.mockarooParamsContainer = mockarooParamsContainer;
    }

    public static final String KEY = "key";

    public Optional<byte[]> downloadFileFromMockaroo() {
        log.info("Creating an Http Request.");
        HttpRequest request =
          HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(buildUrlWithParams(mockarooParamsContainer)))
            .header("accept", MediaType.ALL_VALUE)
            .timeout(Duration.ofSeconds(mockarooParamsContainer.getTimeout()))
            .build();

        log.info("Sending the request to the Mockaroo.");
        HttpResponse<byte[]> response = null;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            log.error("An error occurred while reading file from Mockaroo");
            e.printStackTrace();
        }

        if (Objects.nonNull(response)) {
            return Optional.of(response.body());
        }
        return Optional.empty();
    }

    private String buildUrlWithParams(final MockarooParamsContainer mockarooParamsContainer) {
        log.info("Building URL with params.");
        return UriComponentsBuilder.fromHttpUrl(mockarooParamsContainer.getUrl())
          .queryParam(KEY, mockarooParamsContainer.getKey())
          .build()
          .toUriString();
    }
}
