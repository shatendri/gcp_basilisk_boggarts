package com.drohobytskyy.gcs.service;

public interface CarrierService {

  boolean isEnabled();

  Integer getProcessingInterval();

  String getMockarooUrl();

  String getMockarooKey();

  Integer getMockarooRows();

  void fetchAndUploadFileToBucket(String url, String key, int rows);

  void reLaunchProcessor(
      final String url,
      final String key,
      final int rows,
      final boolean enabled,
      final int processingInterval
  );
}
