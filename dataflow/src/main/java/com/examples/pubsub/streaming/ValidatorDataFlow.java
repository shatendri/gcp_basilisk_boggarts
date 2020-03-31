package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.google.api.services.bigquery.model.TableRow;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;

public class ValidatorDataFlow {

    private final static Logger LOG = LoggerFactory.getLogger(ValidatorDataFlow.class);

    static void runLocalValidatorDataFlow(ValidatorDataFlowOptions options) throws IOException {
        GoogleCredentials credentials = ServiceAccountCredentials.fromStream(new FileInputStream(options.getKeyFilePath()))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));
        options.setGcpCredential(credentials);

        Pipeline pipeline = Pipeline.create(options);
        String subscription = "projects/" + options.getProject() + "/subscriptions/" + options.getSubscription();
        LOG.info("Reading from subscription: " + subscription);
        PCollection<String> messages = pipeline.apply("GetPubSub", PubsubIO.readStrings()
                .fromSubscription(subscription));

        PCollection<String> validMessages = messages.apply("FilterValidMessages", ParDo.of(new JsonToUserDto()));

        // Write to BigQuery

        //Uncomment after creating BigQuery on GCP
        ObjectMapper mapper = new ObjectMapper();
        JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema schema = schemaGen.generateSchema(UserDto.class);
        LOG.info("Write to BigQuery " + validMessages.toString());
        PCollection<TableRow> tableRow = validMessages.apply("ToTableRow", ParDo.of(new PrepData.ToTableRow()));
        tableRow.apply("WriteToBQ",
                BigQueryIO.writeTableRows()
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .to(String.format("%s.%s", options.getBqDataSet(), options.getBqTable()))
                        .skipInvalidRows()
                        .withJsonSchema(schema.toString())
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
        LOG.info("Writing completed");

        pipeline.run().waitUntilFinish();

    }
}