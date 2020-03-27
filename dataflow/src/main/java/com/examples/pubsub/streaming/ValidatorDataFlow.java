package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.UserDto;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class ValidatorDataFlow {

    private final static Logger LOG = LoggerFactory.getLogger(ValidatorDataFlow.class);

    static void runLocalValidatorDataFlow(ValidatorDataFlowOptions options) throws JsonMappingException {
        options.setTempLocation("gs://gcp-trainings/dataflow/");

        Pipeline pipeline = Pipeline.create(options);
        String topic = "projects/my-project-oril/topics/" + options.getInputTopic();
        String subscription = "projects/my-project-oril/subscriptions/" + options.getSubscription();
        LOG.info("Reading from subscription: " + subscription);
        PCollection<String> messages = pipeline.apply("GetPubSub", PubsubIO.readStrings().fromSubscription(subscription));

       // temporary logic for creating data for application in memory instead PupSub
        List<String> keyNames = Arrays.asList("{'email': 'email@gmail.com', 'gender': 'male', 'ip_address': '0.0.0.0', 'first_name': 'Max', 'last_name': 'Payne'}");
        pipeline.apply("GetInMemory", Create.of(keyNames)).setCoder(StringUtf8Coder.of())
                .apply("StringToEntity", ParDo.of(new JsonToUserDto()));

        // Write to BigQuery

        //Uncomment after creating BigQuery on GCP
//        ObjectMapper mapper = new ObjectMapper();
//        JsonSchema schema = mapper.generateJsonSchema(UserDto.class);
//        LOG.info("Write to BigQuery " + messages.toString());
//        PCollection<TableRow> tableRow = messages.apply("ToTableRow", ParDo.of(new PrepData.ToTableRow()));
//        tableRow.apply("WriteToBQ",
//                BigQueryIO.writeTableRows()
//                        .to(String.format("%1$s.%2$s", "dataflow", "dataflow"))
//                        .withJsonSchema(schema.toString())
//                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
//        LOG.info("Writing completed");

        pipeline.run().waitUntilFinish();

    }
}