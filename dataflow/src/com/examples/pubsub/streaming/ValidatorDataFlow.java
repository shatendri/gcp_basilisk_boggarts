package com.examples.pubsub.streaming;

import com.examples.pubsub.streaming.dto.TestDto;
import org.apache.beam.examples.common.WriteOneFilePerWindow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.gcp.util.Transport;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.joda.time.Duration;

import java.io.IOException;

public class ValidatorDataFlow {
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
        @Default.String("test")
        String getSubscription();

        void setSubscription(String value);

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
        options.setTempLocation("gs://gcp-trainings/dataflow/");

        Pipeline p = Pipeline.create(options);
        String topic = "projects/my-project-oril/topics/" + options.getInputTopic();
        String subscription = "projects/my-project-oril/subscriptions/" + options.getSubscription();
        p.apply("GetPubSub", PubsubIO.readStrings().fromSubscription(subscription))
                // 2) Group the messages into fixed-sized minute intervals.
                .apply(Window.into(FixedWindows.of(Duration.standardMinutes(1))))
                // 3) Write one file to GCS for every window of messages.
                .apply("Write Files to GCS", new WriteOneFilePerWindow(options.getOutput(), 1));


        p.run().waitUntilFinish();
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
}