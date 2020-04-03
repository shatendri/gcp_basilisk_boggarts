package com.drohobytskyy.gcs.service;

import com.drohobytskyy.gcs.validator.RegexValidator;
import java.util.Collections;
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
    public static final String FIELD_NAME_IS_SUPPOSED_TO_WORK = "isSupposedToWork";
    public static final String FIELD_NAME_SUPPOSED_INTERVAL = "supposedInterval";
    private static final String REGEX_RANGE = ("^(100|[1-9][0-9]?)$");
    private static final String REGEX_BOOLEAN = "(true|false)";
    private final MockarooReader mockarooReader;
    private final FileNameGenerator fileNameGenerator;
    private final CloudStorageReader storageReader;
    private final CloudStorageWriter storageWriter;
    private final RegexValidator validator;
    private volatile boolean isRunning = false;
    private volatile int interval;

    @Autowired
    public CarrierServiceImpl(
      final MockarooReader mockarooReader,
      final FileNameGenerator fileNameGenerator,
      final CloudStorageReader storageReader,
      final CloudStorageWriter storageWriter,
      final RegexValidator validator) {
        this.mockarooReader = mockarooReader;
        this.fileNameGenerator = fileNameGenerator;
        this.storageReader = storageReader;
        this.storageWriter = storageWriter;
        this.validator = validator;
    }

    public Map<String, Object> homePage(final Map<String, Object> model) {
        if (isRunning) {
            model.put("isRunning", true);
        }
        return model;
    }

    public Map<String, Object> processButtonPush(
      final String isSupposedToWork,
      final String supposedInterval,
      final Map<String, Object> model) {

        model.remove("errors");

        // Incorrect boolean switcher
        if (!validator.validate(isSupposedToWork, Collections.singletonList(REGEX_BOOLEAN))) {
            model.put("errors", FIELD_NAME_IS_SUPPOSED_TO_WORK);
        } else {
            // Turning OFF app
            if (!Boolean.parseBoolean(isSupposedToWork)) {
                isRunning = false;
            } else {
                if (!validator.validate(supposedInterval, Collections.singletonList(REGEX_RANGE))) {
                    // Invalid interval
                    model.put("errors", FIELD_NAME_SUPPOSED_INTERVAL);
                } else {
                    // Turning ON app
                    interval = Integer.parseInt(supposedInterval);
                    model.put("interval", supposedInterval);
                    if (!isRunning) {
                        // running new Timer
                        isRunning = true;
                        processRequestInLoop();
                    }
                }
            }
        }
        model.put("isRunning", isRunning);
        return model;
    }

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
                          e.printStackTrace();
                      }
                  }
              }
          },
          DELAY_BEFORE_START
        );
    }

}
