package com.drohobytskyy.gcs.service;

public interface CarrierService {

  boolean isEnabled();

  Integer getProcessingInterval();

  String getMockarooUrl();

  String getMockarooKey();

  void fetchAndUploadFileToBucket(String url, String key);

  void reLaunchProcessor(
      final String url,
      final String key,
      final boolean enabled,
      final int processingInterval
  );
}
