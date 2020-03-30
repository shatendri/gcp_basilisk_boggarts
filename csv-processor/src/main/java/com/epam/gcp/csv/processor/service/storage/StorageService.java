package com.epam.gcp.csv.processor.service.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface StorageService {

  Path store(InputStream inputStream, String pathToFileInBucket) throws IOException;
}
