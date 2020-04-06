package com.drohobytskyy.gcs.controller;

import com.drohobytskyy.gcs.mockaroo.client.MockarooClient;
import com.drohobytskyy.gcs.service.CarrierService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestController
@RequestMapping(value = "/")
public class CarrierController {

    public static final String FIELD_NAME_PROCESSING_INTERVAL = "processingInterval";
    public static final int MIN_INTERVAL = 1;
    public static final int MAX_INTERVAL = 100;

    private final CarrierService carrierService;
    private MockarooClient mockarooClient;

    public CarrierController(
      final CarrierService carrierService,
      MockarooClient mockarooClient) {
        this.carrierService = carrierService;
        this.mockarooClient = mockarooClient;
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
        List<String> errorList = new LinkedList<>();

        if (!(processingInterval >= MIN_INTERVAL && processingInterval <= MAX_INTERVAL)) {
            errorList.add(FIELD_NAME_PROCESSING_INTERVAL);
        }
        if (!isValidMockarooUrl(mocakarooUrl, mocakarooKey)) {
            errorList.add("Invalid Mockaroo URL or/and key.");
        }

        if (errorList.isEmpty()) {
            carrierService.reLaunchProcessor(mocakarooUrl, mocakarooKey, enabled, processingInterval);
        }
        fillModel(model, errorList);

        return new ModelAndView("home", model);
    }

    @ResponseBody
    @PostMapping("/gcs")
    public ResponseEntity<?> fetchAndUploadFileToBucketManually(
      @RequestParam(required = false) final String url,
      @RequestParam(required = false) final String key
    ) {
        carrierService.fetchAndUploadFileToBucket(url, key);
        return ResponseEntity.ok().build();
    }

    private void fillModel(Map<String, Object> model) {
        model.put("isEnabled", carrierService.isEnabled());
        model.put("mockarooUrl", carrierService.getMockarooUrl());
        model.put("mockarooKey", carrierService.getMockarooKey());
        model.put("processingInterval", carrierService.getProcessingInterval());
        model.put("minInterval", MIN_INTERVAL);
        model.put("maxInterval", MAX_INTERVAL);
    }

    private void fillModel(Map<String, Object> model, final List<String> errorList) {
        fillModel(model);
        model.put("errors", errorList);
    }

    private boolean isValidMockarooUrl(String mocakarooUrl, String mocakarooKey) {
        log.info("Validating Mockaroo URL and Key");
        try {
            final byte[] mockarooFileContent =
              mockarooClient.loadFile(mocakarooUrl, mocakarooKey);
        } catch (Throwable e) {
            log.error("An error occurred while trying to read data from Mockaroo", e);
            return false;
        }
        return true;
    }
}
