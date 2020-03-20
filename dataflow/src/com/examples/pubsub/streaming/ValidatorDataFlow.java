package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TestDto;
import org.apache.beam.examples.common.WriteOneFilePerWindow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.gcp.util.Transport;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.PCollection;
import org.joda.time.Duration;

import java.io.IOException;

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
        p.apply(TextIO.read().from(options.getInputFile()))
                .apply(MapElements.via(new ParseJson()))
                .apply(new ObjectValidation())
                .apply("Write", TextIO.write().to(options.getOutput()));
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
}