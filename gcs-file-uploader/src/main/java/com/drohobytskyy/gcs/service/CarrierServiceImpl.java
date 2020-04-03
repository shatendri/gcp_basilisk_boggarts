package com.drohobytskyy.gcs.service;

import com.drohobytskyy.gcs.mockaroo.client.MockarooClient;
import com.drohobytskyy.gcs.service.storage.CloudStorageService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class CarrierServiceImpl implements CarrierService {

  public static final int MILLISECONDS_IN_SECOND = 1000;
  public static final int DELAY_BEFORE_START = 3000;

  private final MockarooClient mockarooClient;
  private final CloudStorageService cloudStorageService;

  private static AtomicBoolean ENABLED = new AtomicBoolean(false);
  private static AtomicInteger PROCESSING_INTERVAL = new AtomicInteger(30);

  private static AtomicReference<String> MOCKAROO_URL =
      new AtomicReference<>("https://my.api.mockaroo.com/gcs.json");
  private static AtomicReference<String> MOCKAROO_KEY =
      new AtomicReference<>("f474aa20");

  public CarrierServiceImpl(
      final MockarooClient mockarooClient,
      final CloudStorageService cloudStorageService
  ) {
    this.mockarooClient = mockarooClient;
    this.cloudStorageService = cloudStorageService;
  }

  @Override
  public boolean isEnabled() {
    return ENABLED.get();
  }

  @Override
  public Integer getProcessingInterval() {
    return PROCESSING_INTERVAL.get();
  }

  @Override
  public String getMockarooUrl() {
    return MOCKAROO_URL.get();
  }

  @Override
  public String getMockarooKey() {
    return MOCKAROO_KEY.get();
  }

  @Override
  public void fetchAndUploadFileToBucket(String url, String key) {
    if (StringUtils.isEmpty(url) || StringUtils.isEmpty(key)) {
      url = MOCKAROO_URL.get();
      key = MOCKAROO_KEY.get();
    } else {
      MOCKAROO_URL.set(url);
      MOCKAROO_KEY.set(key);
    }

    try {
      final byte[] mockarooFileContent = mockarooClient.loadFile(url, key);
      cloudStorageService.store(mockarooFileContent, buildFileName());
    } catch (Exception e) {
      log.error("Cannot fetch and/or store file to storage", e);
    }
  }

  @Override
  public void reLaunchProcessor(
      final String url,
      final String key,
      final boolean enabled,
      final int processingInterval
  ) {
    // Turning OFF app
    if (!enabled) {
      ENABLED.set(false);
    } else {
      // Turning ON app
      PROCESSING_INTERVAL.set(processingInterval);
      if (!ENABLED.get()) {
        // running new Timer
        ENABLED.set(true);
        startProcessorTimer(url, key);
      }
    }
  }

  private void startProcessorTimer(final String url, final String key) {
    new Timer().schedule(
        new TimerTask() {
          @Override
          public void run() {
            while (ENABLED.get()) {
              log.info("------------- " + "Task performed on " + new Date() + "  -------------");
              fetchAndUploadFileToBucket(url, key);
              try {
                Thread.sleep(PROCESSING_INTERVAL.get() * MILLISECONDS_IN_SECOND);
              } catch (final InterruptedException e) {
                log.error("An error occurred while tying to sleep.", e);
              }
            }
          }
        },
        DELAY_BEFORE_START
    );
  }

  private String buildFileName() {
    final String dateAsString =
        LocalDateTime.now()
            .format(DateTimeFormatter.ISO_LOCAL_DATE);

    return String.format("Mockaroo_%s_%s.csv", dateAsString, UUID.randomUUID().toString());
  }
}
