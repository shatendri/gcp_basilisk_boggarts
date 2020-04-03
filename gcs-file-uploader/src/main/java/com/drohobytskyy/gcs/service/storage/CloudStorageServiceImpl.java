package com.drohobytskyy.gcs.service.storage;

import com.google.cloud.ReadChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CloudStorageServiceImpl implements CloudStorageService {

  private final Storage storage;
  private final String bucketName;

  public CloudStorageServiceImpl(
      final Storage storage,
      @Value("${spring.cloud.gcp.storage.bucket}") final String bucketName
  ) {
    this.storage = storage;
    this.bucketName = bucketName;
  }

  public void store(final byte[] data, final String fileName) {
    log.info("Started file {} uploading data to Cloud Storage", fileName);

    final BlobId blobId = BlobId.of(bucketName, fileName);
    final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    storage.create(blobInfo, data);

    log.info("File {} uploaded to Cloud Storage", fileName);
  }

  public String read(final String fileName) throws IOException {
    log.info("Retrieving file from Cloud Storage.");

    final StringBuilder builder = new StringBuilder();
    final ReadChannel channel = storage.reader(bucketName, fileName);
    final ByteBuffer bytes = ByteBuffer.allocate(64 * 1024);

    while (channel.read(bytes) > 0) {
      bytes.flip();
      builder.append(new String(bytes.array(), 0, bytes.limit()));
      bytes.clear();
    }

    return builder.toString();
  }
}
