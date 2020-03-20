package com.epam.gcp.csv.processor.pubsub.message.receiver;

import com.epam.gcp.csv.processor.pubsub.message.publisher.PubSubMessagePublisher;
import com.epam.gcp.csv.processor.csv.parser.CsvParserTask;
import com.epam.gcp.csv.processor.service.storage.StorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.storage.GoogleStorageResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
public class PubSubMessageReceiver implements MessageHandler {

  private static final String EVENT_TYPE_KEY = "eventType";
  private static final String OBJECT_FINALIZE_EVENT_TYPE_VALUE = "OBJECT_FINALIZE";
  private static final String BUCKET_NAME_KEY = "bucket";
  private static final String FILE_PATH_KEY = "name";

  private static final String CSV_FILE_EXTENSION = ".csv";

  private final ObjectMapper objectMapper;
  private final ResourceLoader resourceLoader;
  private final StorageService storageService;
  private final ThreadPoolTaskExecutor applicationTaskExecutor;
  private final PubSubMessagePublisher pubSubMessagePublisher;

  public PubSubMessageReceiver(
      ObjectMapper objectMapper,
      ResourceLoader resourceLoader,
      StorageService storageService,
      ThreadPoolTaskExecutor applicationTaskExecutor,
      PubSubMessagePublisher pubSubMessagePublisher
  ) {
    this.objectMapper = objectMapper;
    this.resourceLoader = resourceLoader;
    this.storageService = storageService;
    this.applicationTaskExecutor = applicationTaskExecutor;
    this.pubSubMessagePublisher = pubSubMessagePublisher;
  }

  @Override
  public void handleMessage(Message<?> message) throws MessagingException {
    String eventType = (String) message.getHeaders().get(EVENT_TYPE_KEY);
    if (OBJECT_FINALIZE_EVENT_TYPE_VALUE.equalsIgnoreCase(eventType)) {
      String payloadAsString = new String((byte[]) message.getPayload(), StandardCharsets.UTF_8);
      log.debug("Notification received! Payload: {}", payloadAsString);
      try {
        Map<String, Object> payload =
            objectMapper.readValue(
                payloadAsString,
                new TypeReference<LinkedHashMap<String, Object>>() {
                }
            );

        String bucketName = (String) payload.get(BUCKET_NAME_KEY);
        String pathToFile = (String) payload.get(FILE_PATH_KEY);

        if (pathToFile.toLowerCase().endsWith(CSV_FILE_EXTENSION.toLowerCase())) {
          Path pathToSavedFile = downloadAndStoreFile(bucketName, pathToFile);

          applicationTaskExecutor.execute(
              new CsvParserTask(
                  pubSubMessagePublisher,
                  pathToSavedFile
              )
          );
        }
      } catch (Exception e) {
        log.error("Error during file processing!", e);
      }
    }
  }

  private Path downloadAndStoreFile(String bucketName, String pathToFile) throws IOException {
    String storagePath = String.format("gs://%s/%s", bucketName, pathToFile);

    log.info("Started '{}' file downloading...", storagePath);

    GoogleStorageResource googleStorageResource =
        (GoogleStorageResource) resourceLoader.getResource(storagePath);

    Path pathToSavedFile =
        storageService.store(googleStorageResource.getInputStream(), pathToFile);

    log.info("File '{}' downloaded to '{}'!", storagePath, pathToSavedFile.toString());

    return pathToSavedFile;
  }
}
