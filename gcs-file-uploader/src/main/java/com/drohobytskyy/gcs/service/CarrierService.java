package com.drohobytskyy.gcs.service;

import java.util.Map;

public interface CarrierService {

    Map<String, Object> homePage(final Map<String, Object> model);

    String processRequest();

    Map<String, Object> processButtonPush(
      final boolean isSupposedToWork,
      final int supposedInterval,
      final Map<String, Object> model);

}
