package com.drohobytskyy.gcs.service;

public interface CarrierService {

    boolean isEnabled();

    Integer getProcessingInterval();

    String getMockarooUrl();

    String getMockarooKey();

    Integer getMockarooRows();

    void fetchAndUploadFileToBucket(String url, String key, int countOfRaws);

    void reLaunchProcessor(
      final String url,
      final String key,
      final int countOfRaws,
      final boolean enabled,
      final int processingInterval
    );
}
