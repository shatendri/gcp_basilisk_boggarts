package com.drohobytskyy.gcs.service;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CloudStorageReader {

    @Autowired
    Storage storage;
    @Value("${spring.cloud.gcp.storage.bucket}")
    String bucketName;

    public String readFileFromGCS(final String filename) {
        log.info("Retrieving file from Cloud Storage.");
        StringBuilder buffer = new StringBuilder();
        try {
            ReadChannel channel = storage.reader(bucketName, filename);
            ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);
            while (channel.read(bytes) > 0) {
                bytes.flip();
                buffer.append(new String(bytes.array(), 0, bytes.limit()));
                bytes.clear();
            }
        } catch (IOException e) {
            log.error("An error occurred while reading file from Cloud Storage");
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
