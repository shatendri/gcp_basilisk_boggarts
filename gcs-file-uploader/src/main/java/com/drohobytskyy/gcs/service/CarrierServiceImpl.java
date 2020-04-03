package com.drohobytskyy.gcs.service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CarrierServiceImpl implements CarrierService {

    public static final int MILLISECONDS_IN_SECOND = 1000;
    public static final int DELAY_BEFORE_START = 3000;
    private final MockarooReader mockarooReader;
    private final FileNameGenerator fileNameGenerator;
    private final CloudStorageReader storageReader;
    private final CloudStorageWriter storageWriter;
    private volatile boolean isRunning = false;
    private volatile int interval;

    @Autowired
    public CarrierServiceImpl(
      final MockarooReader mockarooReader,
      final FileNameGenerator fileNameGenerator,
      final CloudStorageReader storageReader,
      final CloudStorageWriter storageWriter) {
        this.mockarooReader = mockarooReader;
        this.fileNameGenerator = fileNameGenerator;
        this.storageReader = storageReader;
        this.storageWriter = storageWriter;
    }

    @Override
    public Map<String, Object> homePage(final Map<String, Object> model) {
        if (isRunning) {
            model.put("isRunning", true);
        }
        return model;
    }

    @Override
    public Map<String, Object> processButtonPush(
      final boolean isSupposedToWork,
      final int supposedInterval,
      final Map<String, Object> model) {

        model.remove("errors");

        // Turning OFF app
        if (!isSupposedToWork) {
            isRunning = false;
        } else {
            // Turning ON app
            interval = supposedInterval;
            model.put("interval", interval);
            if (!isRunning) {
                // running new Timer
                isRunning = true;
                processRequestInLoop();
            }
        }
        model.put("isRunning", isRunning);
        return model;
    }

    @Override
    public String processRequest() {
        final Optional<byte[]> dataFromMockaroo = mockarooReader.downloadFileFromMockaroo();
        final String filename = fileNameGenerator.generateFileName();
        storageWriter.writeFileToGCS(dataFromMockaroo, filename);

        return storageReader.readFileFromGCS(filename);
    }

    private void processRequestInLoop() {
        new Timer().schedule(
          new TimerTask() {
              @Override
              public void run() {
                  while (isRunning) {
                      log.info("------------- " + "Task performed on " + new Date() + "  -------------");
                      processRequest();
                      try {
                          Thread.sleep(interval * MILLISECONDS_IN_SECOND);
                      } catch (final InterruptedException e) {
                          log.error("An error occurred while tying to sleep.", e);
                      }
                  }
              }
          },
          DELAY_BEFORE_START
        );
    }

}
