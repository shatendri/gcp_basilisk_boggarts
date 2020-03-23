package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TestDto;
import com.google.api.services.bigquery.model.TableRow;
import com.google.datastore.v1.ArrayValue;
import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Value;
import org.apache.beam.examples.common.WriteOneFilePerWindow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.gcp.util.Transport;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidatorDataFlow {
    public interface ValidatorDataFlowOptions extends PipelineOptions, StreamingOptions {
        @Description("The Local file to read from.")
        @Default.String("C:\\Users\\Miha\\dataflow\\test.json")
        String getInputFile();

        void setInputFile(String value);

        @Description("The Cloud Pub/Sub topic to read from.")
        @Default.String("test")
        String getInputTopic();

        void setInputTopic(String value);

        @Description("Output file's window size in number of minutes.")
        @Default.Integer(1)
        Integer getWindowSize();

        void setWindowSize(Integer value);

        @Description("Path of the output file including its filename prefix.")
        @Required
        @Default.String("C:\\Users\\Miha\\dataflow\\test2.txt")
        String getOutput();

        void setOutput(String value);

        @Description("Output destination Datastore Kind")
        @Default.String("hogeKind")
        ValueProvider<String> getOutputKind();

        void setOutputKind(ValueProvider<String> value);

    }

    public static void main(String[] args) throws IOException {
        ValidatorDataFlowOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(ValidatorDataFlowOptions.class);
        runLocalValidatorDataFlow(options);
    }

    static void runLocalValidatorDataFlow(ValidatorDataFlowOptions options) {
        Pipeline p = Pipeline.create(options);
        String kind = options.getOutputKind().get();
        p.apply(TextIO.read().from(options.getInputFile()))
                .apply(MapElements.via(new ParseJson()))
                .apply(new ObjectValidation())
                .apply("Write", TextIO.write().to(options.getOutput()));


        PCollection<KV<Integer, Iterable<TableRow>>> keywordGroups = p
                .apply(BigQueryIO.Read.named("ReadUtterance").from(inputTable)).apply(new GroupKeywords());

        CreateEntities createEntities = new CreateEntities();
        createEntities.setKind(kind);

        PCollection<Entity> entities = keywordGroups.apply(createEntities);
        entities.apply(DatastoreIO.v1().write().withProjectId(""));
        p.run().waitUntilFinish();
    }

    static void runGCPValidatorDataFlow(ValidatorDataFlowOptions options) {
        // The maximum number of shards when writing output.
        int numShards = 1;
        Pipeline pipeline = Pipeline.create(options);
        pipeline
                // 1) Read string messages from a Pub/Sub topic.
                .apply("Read PubSub Messages",
                        PubsubIO.readStrings().fromTopic(options.getInputTopic()))
                .apply(Window.into(FixedWindows.of(Duration.standardMinutes(options.getWindowSize()))))
                .apply("Write Files to GCS", new WriteOneFilePerWindow(options.getOutput(), numShards));
        pipeline.run().waitUntilFinish();
    }

    static class ParseJson extends SimpleFunction<String, TestDto> {
        @Override
        public TestDto apply(String input) {
            try {
                return Transport.getJsonFactory().fromString(input, TestDto.class);
            } catch (IOException e) {
                throw new RuntimeException("Failed parsing table row json", e);
            }
        }
    }

    private static class ObjectValidation
            extends PTransform<PCollection<TestDto>, PCollection<String>> {
        @Override
        public PCollection<String> expand(PCollection<TestDto> input) {
            return input
                    .apply(ParDo.of(new ExtractUserAndTimestamp()));
        }
    }

    static class ExtractUserAndTimestamp extends DoFn<TestDto, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element().toString());
        }
    }

    static class CreateEntities extends PTransform<PCollection<KV<Integer, Iterable<TableRow>>>, PCollection<Entity>> {

        String kind = "";

        public void setKind(String kind){
            this.kind = kind;
        }

        @Override
        public PCollection<Entity> expand(PCollection<KV<Integer, Iterable<TableRow>>> input) {

            CreateEntityFn f = new CreateEntityFn();
            f.setKind(this.kind);

            return input.apply(ParDo.of(f));
        }
    }

    static class CreateEntityFn extends DoFn<KV<Integer, Iterable<TableRow>>, Entity> {

        String kind = "";

        public void setKind(String kind) {
            this.kind = kind;
        }

        public Entity makeEntity(KV<Integer, Iterable<TableRow>> content) {

            Key key = Key.newBuilder()
                    .addPath(Key.PathElement.newBuilder().setKind(this.kind).setId(content.getKey())).build();

            String keyword = "";
            List<Value> list = new ArrayList<>();
            for (TableRow row : content.getValue()) {
                String utterance = row.get("utterance").toString();
                if (utterance == null || utterance.length() < 1) {
                    continue;
                }
                String word = row.get("keyword").toString();
                if (keyword.equals(row.get("keyword")) == false) {
                    keyword = word;
                }
                if (list.size() > 1000) {
                    break;
                }
                list.add(Value.newBuilder().setStringValue(utterance).build());
            }

            Entity.Builder entityBuilder = Entity.newBuilder();
            entityBuilder.setKey(key);

            Map<String, Value> propertyMap = new HashMap<String, Value>();
            propertyMap.put("KeywordID", Value.newBuilder().setIntegerValue(content.getKey()).build());
            propertyMap.put("Keyword", Value.newBuilder().setStringValue(keyword).build());
            ArrayValue array = ArrayValue.newBuilder().addAllValues(list).build();
            propertyMap.put("Candidates", Value.newBuilder().setArrayValue(array).build());

            entityBuilder.putAllProperties(propertyMap);

            return entityBuilder.build();
        }

        @Override
        public void processElement(ProcessContext c) {
            c.output(makeEntity(c.element()));
        }
    }
}