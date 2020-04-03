package com.examples.pubsub.streaming;

import org.apache.beam.sdk.options.PipelineOptionsFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        ValidatorDataFlowOptions options = PipelineOptionsFactory
                .fromArgs(args)
                .withValidation()
                .as(ValidatorDataFlowOptions.class);
        ValidatorDataFlow.runLocalValidatorDataFlow(options);
    }
}
