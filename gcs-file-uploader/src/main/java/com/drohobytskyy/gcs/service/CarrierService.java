package com.drohobytskyy.gcs.service;

import java.util.Map;

public interface CarrierService {

    Map<String, Object> homePage(final Map<String, Object> model);

    String processRequest();

    Map<String, Object> processButtonPush(
      final String isSupposedToWork,
      final String supposedInterval,
      final Map<String, Object> model);

}
