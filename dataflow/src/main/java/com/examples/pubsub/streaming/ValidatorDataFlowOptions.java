package com.examples.pubsub.streaming;

import org.apache.beam.sdk.extensions.gcp.options.GcpOptions;
import org.apache.beam.sdk.options.*;

public interface ValidatorDataFlowOptions extends StreamingOptions, GcpOptions {
    @Description("The Cloud Pub/Sub subscription to read from.")
    @Default.String("dataflow-job")
    String getSubscription();

    void setSubscription(String value);

    @Description("GCP service account key location")
    @Default.String("GCP service account key location in .json format")
    String getKeyFilePath();

    void setKeyFilePath(String keyFilePath);

    @Description("BigQuery dataset name")
    @Default.String("dataflow")
    String getBqDataSet();
    void setBqDataSet(String dataSet);

    @Description("BigQuery table name")
    @Default.String("dataflow")
    String getBqTable();
    void setBqTable(String table);
}
