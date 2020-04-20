package com.epam.gcp.csv.processor.csv.parser;

import com.epam.gcp.csv.processor.pubsub.message.publisher.PubSubMessagePublisher;
import com.google.gson.JsonObject;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvParserTask implements Runnable {

  private static final char FILE_SEPARATOR = ',';
  private static final char SKIP_LINES = 0;
  private static final boolean IGNORE_QUOTATIONS = true;

  private final PubSubMessagePublisher pubSubMessagePublisher;
  private final Path pathToFile;

  public CsvParserTask(PubSubMessagePublisher pubSubMessagePublisher, Path pathToFile) {
    this.pubSubMessagePublisher = pubSubMessagePublisher;
    this.pathToFile = pathToFile;
  }

  @Override
  public void run() {
    log.info("Started CSV file parsing: {}", pathToFile.toString());

    try (Reader reader = new FileReader(pathToFile.toFile())) {
      CSVReader csvReader = buildCSVReader(reader);
      String[] names = csvReader.readNext();
      String[] line;
      while ((line = csvReader.readNext()) != null) {
        if (names.length != line.length) {
          log.warn("Names length and Values length don't match!");
          continue;
        }
        publishToPubSub(toJson(names, line));
      }

      csvReader.close();
    } catch (IOException | CsvValidationException e) {
      log.error("CSV processing error!", e);
    }

    log.info("Finished CSV file parsing: {}", pathToFile.toString());
  }

  private CSVReader buildCSVReader(Reader reader) {
    CSVParser parser =
        new CSVParserBuilder()
            .withSeparator(FILE_SEPARATOR)
            .withIgnoreQuotations(IGNORE_QUOTATIONS)
            .build();

    return
        new CSVReaderBuilder(reader)
            .withSkipLines(SKIP_LINES)
            .withCSVParser(parser)
            .build();
  }

  private String toJson(String[] names, String[] values) {
    JsonObject jsonObject = new JsonObject();
    for (int i = 0; i < names.length; i++) {
      jsonObject.addProperty(names[i], values[i]);
    }
    return jsonObject.toString();
  }

  private void publishToPubSub(String json) {
    log.debug("PubSub json publishing: {}", json);
    pubSubMessagePublisher.publish(json);
  }
}
