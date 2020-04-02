package com.drohobytskyy.gcs.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CloudStorageWriter {

    @Autowired
    Storage storage;
    @Value("${spring.cloud.gcp.storage.bucket}")
    String bucketName;

    public void writeFileToGCS(final Optional<byte[]> data, final String filename) {

        data.ifPresentOrElse(
          (byteArray)
            -> {
              log.info("Uploading data to Cloud Storage.");
              BlobId blobId = BlobId.of(bucketName, filename);
              BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
              storage.create(blobInfo, byteArray);
          },
          ()
            -> {
              log.error("No data from Mockaroo. Uploading cancelled.");
          });
    }

}
