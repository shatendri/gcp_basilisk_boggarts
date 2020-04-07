package com.drohobytskyy.gcs.mockaroo.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class MockarooClientImpl implements MockarooClient {

    private static final String KEY = "key";
    private static final String COUNT_OF_ROWS = "countOfRows";

    private final HttpClient httpClient;
    private final Integer timeout;

    public MockarooClientImpl(
      final HttpClient mockarooHttpClient,
      @Value("${mockaroo.timeout}") final Integer timeout
    ) {
        this.httpClient = mockarooHttpClient;
        this.timeout = timeout;
    }

    @Override
    public byte[] loadFile(String url, String key, int countOfRows) throws IOException, InterruptedException {

        final URI uri =
          UriComponentsBuilder.fromHttpUrl(url)
            .queryParam(KEY, key)
            .queryParam(COUNT_OF_ROWS, countOfRows)
            .build()
            .toUri();

        final HttpRequest request =
          HttpRequest.newBuilder()
            .GET()
            .uri(uri)
            .header(HttpHeaders.ACCEPT, MediaType.ALL_VALUE)
            .timeout(Duration.ofSeconds(timeout))
            .build();

        final HttpResponse<byte[]> response =
          httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        log.debug("Request: {}\nStatus code: {}", response.request().toString(), response.statusCode());

        handleHttpErrors(response);

        return response.body();
    }

    private void handleHttpErrors(final HttpResponse response) {
        final HttpStatus httpStatus = HttpStatus.valueOf(response.statusCode());
        if (!httpStatus.is2xxSuccessful()) {
            throw new HttpClientErrorException(httpStatus, response.body().toString());
        }
    }
}
