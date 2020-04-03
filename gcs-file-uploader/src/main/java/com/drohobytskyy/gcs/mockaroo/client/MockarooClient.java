package com.drohobytskyy.gcs.mockaroo.client;

import java.io.IOException;

public interface MockarooClient {

  byte[] loadFile(String url, String key) throws IOException, InterruptedException;
}
