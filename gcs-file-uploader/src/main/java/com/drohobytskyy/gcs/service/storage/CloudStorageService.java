package com.drohobytskyy.gcs.service.storage;

import java.io.IOException;

public interface CloudStorageService {

    String read(final String fileName) throws IOException;

    void store(final byte[] data, final String fileName);
}
