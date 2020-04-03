package com.drohobytskyy.gcs.controller;

import com.drohobytskyy.gcs.service.CarrierService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping(value = "/")
public class CarrierController {

  public static final String FIELD_NAME_SUPPOSED_INTERVAL = "processingInterval";

  public static final int MIN_INTERVAL = 1;
  public static final int MAX_INTERVAL = 100;

  private final CarrierService carrierService;

  public CarrierController(final CarrierService carrierService) {
    this.carrierService = carrierService;
  }

  @GetMapping
  public ModelAndView homePage(final Map<String, Object> model) {
    fillModel(model);
    return new ModelAndView("home", model);
  }

  @PostMapping
  public ModelAndView buttonPush(
      @RequestParam final boolean enabled,
      @RequestParam final int processingInterval,
      @RequestParam final String mocakarooUrl,
      @RequestParam final String mocakarooKey,
      final Map<String, Object> model
  ) {

    if (processingInterval >= MIN_INTERVAL && processingInterval <= MAX_INTERVAL) {
      model.remove("errors");
      carrierService.reLaunchProcessor(mocakarooUrl, mocakarooKey, enabled, processingInterval);
      fillModel(model);
    } else {
      model.put("errors", FIELD_NAME_SUPPOSED_INTERVAL);
    }

    return new ModelAndView("home", model);
  }

  // curl -X POST http://localhost:5757/gcs
  @ResponseBody
  @PostMapping("/gcs")
  public ResponseEntity<?> fetchAndUploadFileToBucketManually(
      @RequestParam(required = false) final String url,
      @RequestParam(required = false) final String key
  ) {
    carrierService.fetchAndUploadFileToBucket(url, key);
    return ResponseEntity.ok().build();
  }

  private Map<String, Object> fillModel(Map<String, Object> model) {
    model.put("isEnabled", carrierService.isEnabled());
    model.put("mockarooUrl", carrierService.getMockarooUrl());
    model.put("mockarooKey", carrierService.getMockarooKey());
    model.put("processingInterval", carrierService.getProcessingInterval());
    return model;
  }
}
