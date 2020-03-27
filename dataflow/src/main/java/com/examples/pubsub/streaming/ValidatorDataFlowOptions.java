package com.examples.pubsub.streaming;

import org.apache.beam.sdk.options.*;

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
    @Validation.Required
    @Default.String("C:\\Users\\Viktor_Morozov\\Downloads\\test2.txt")
    String getOutput();

    void setOutput(String value);


    @Description("Table spec to write the output to")
    ValueProvider<String> getOutputTableSpec();

    void setOutputTableSpec(ValueProvider<String> value);
}
