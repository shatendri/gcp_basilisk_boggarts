package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TestDto;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import com.google.api.client.json.Json;
import com.google.api.services.bigquery.model.TableRow;
import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.PartitionId;
import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ValidatorDataFlow {
    private final static Logger LOG = LoggerFactory.getLogger(ValidatorDataFlow.class);
    public interface ValidatorDataFlowOptions extends PipelineOptions, StreamingOptions {
        @Description("The Local file to read from.")
        @Default.String("C:\\Users\\Miha\\dataflow\\test.json")
        String getInputFile();

        void setInputFile(String value);

        @Description("The Cloud Pub/Sub topic to read from.")
        @Default.String("dataflow-json-processing-topic")
        String getInputTopic();

        void setInputTopic(String value);

        @Description("The Cloud Pub/Sub subscription to read from.")
        @Default.String("dataflow-job")
        String getSubscription();

        void setSubscription(String value);

        @Description("Path of the output file including its filename prefix.")
        @Required
        @Default.String("C:\\Users\\Miha\\dataflow\\test2.txt")
        String getOutput();

        void setOutput(String value);

        @Description("Table spec to write the output to")
        ValueProvider<String> getOutputTableSpec();

        void setOutputTableSpec(ValueProvider<String> value);
    }

    public static void main(String[] args) throws IOException {
        ValidatorDataFlowOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(ValidatorDataFlowOptions.class);
        runLocalValidatorDataFlow(options);
    }

    static void runLocalValidatorDataFlow(ValidatorDataFlowOptions options) throws JsonMappingException {
        options.setTempLocation("gs://gcp-trainings/dataflow/");
        ObjectMapper mapper = new ObjectMapper();
        JsonSchema schema = mapper.generateJsonSchema(TestDto.class);


        Pipeline pipeline = Pipeline.create(options);
        String topic = "projects/my-project-oril/topics/" + options.getInputTopic();
        String subscription = "projects/my-project-oril/subscriptions/" + options.getSubscription();
        LOG.info("Reading from subscription: " + subscription);
        PCollection<String> messages = pipeline.apply("GetPubSub", PubsubIO.readStrings().fromSubscription(subscription));


        // Write to BigQuery
        LOG.info("Write to BigQuery " + messages.toString());
        PCollection<TableRow> tableRow = messages.apply("ToTableRow", ParDo.of(new PrepData.ToTableRow()));
        tableRow.apply("WriteToBQ",
                BigQueryIO.writeTableRows()
                        .to(String.format("%1$s.%2$s", "dataflow", "dataflow"))
                        .withJsonSchema(schema.toString())
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));
        LOG.info("Writing completed");
        pipeline.run().waitUntilFinish();
    }


    /**
     * DoFn for converting a Protov3 JSON Encoded Entity to a Datastore Entity.
     * JSON in mapped protov3:
     * https://developers.google.com/protocol-buffers/docs/proto3#json
     */
    public static class JsonToEntity extends DoFn<String, Entity> {
        private EntityJsonParser entityJsonParser;

        @Setup
        public void setup() {
            entityJsonParser = new EntityJsonParser();
        }

        @ProcessElement
        public void processElement(ProcessContext c) throws InvalidProtocolBufferException {
            String entityJson = c.element();
            Entity.Builder entityBuilder = Entity.newBuilder();
            entityJsonParser.merge(entityJson, entityBuilder);

            // Build entity who's key has an empty project Id.
            // This allows DatastoreIO to handle what project Entities are loaded into
            Key k = entityBuilder.build().getKey();
            entityBuilder.setKey(Key.newBuilder()
                    .addAllPath(k.getPathList())
                    .setPartitionId(PartitionId.newBuilder()
                            .setProjectId("")
                            .setNamespaceId(k.getPartitionId().getNamespaceId())));

            c.output(entityBuilder.build());
        }
    }

    /**
     * Converts a JSON String to an Entity.
     */
    public static class EntityJsonParser {

        // A cached jsonParser
        private JsonFormat.Parser jsonParser;

        public EntityJsonParser() {
            JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                    .add(Entity.getDescriptor())
                    .build();

            jsonParser = JsonFormat.parser()
                    .usingTypeRegistry(typeRegistry);
        }

        public void merge(String json, Entity.Builder entityBuilder)
                throws InvalidProtocolBufferException {
            jsonParser.merge(json, entityBuilder);
        }

        public Entity parse(String json) throws InvalidProtocolBufferException {
            Entity.Builder entityBuilter = Entity.newBuilder();
            merge(json, entityBuilter);
            return entityBuilter.build();
        }

    }
}