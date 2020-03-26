package com.examples.pubsub.streaming;

import com.google.api.services.bigquery.model.TableRow;
import com.google.gson.Gson;
import org.apache.beam.repackaged.core.org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.beam.sdk.transforms.DoFn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


public class PrepData {
    public static class ToTableRow extends DoFn<String, TableRow> {
        private final static Logger LOG = LoggerFactory.getLogger(ToTableRow.class);

        @ProcessElement
        public void processElement(ProcessContext c) {
            try {
                String json = c.element();
                Gson gson = new Gson();

                TableRow outputRow = gson.fromJson(json, TableRow.class);
                LOG.debug("Timestamp overwrite to - " + LocalDateTime.now());
                outputRow.set("timestamp", LocalDateTime.now().toString());
                c.output(outputRow);
            } catch (Exception e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
