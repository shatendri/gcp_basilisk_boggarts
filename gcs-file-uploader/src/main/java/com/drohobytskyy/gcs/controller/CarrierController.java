package com.drohobytskyy.gcs.controller;

import com.drohobytskyy.gcs.mockaroo.client.MockarooClient;
import com.drohobytskyy.gcs.service.CarrierService;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    public static final int MIN_COUNT_OF_ROWS = 1;
    public static final int MAX_COUNT_OF_ROWS = 1000;
    public static final String MOCKAROO_URL_OR_AND_KEY = "Mockaroo URL or/and key";
    public static final String MOCAKAROO_ROWS = "mockarooRows";

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
      @RequestParam final int mockarooRows,
      @RequestParam final String mockarooUrl,
      @RequestParam final String mockarooKey,
      final Map<String, Object> model
    ) {
        List<String> errorList = new LinkedList<>();
        validateInputs(enabled, processingInterval, mockarooRows, mockarooUrl, mockarooKey, errorList);
        if (errorList.isEmpty()) {
            carrierService.reLaunchProcessor(mockarooUrl, mockarooKey, mockarooRows, enabled, processingInterval);
        }
        fillModel(model, errorList);

        return new ModelAndView("home", model);
    }

    @ResponseBody
    @PostMapping("/gcs")
    public ResponseEntity<?> fetchAndUploadFileToBucketManually(
      @RequestParam(required = false) final String url,
      @RequestParam(required = false) final String key,
      @RequestParam(required = false) final int raws
    ) {
        carrierService.fetchAndUploadFileToBucket(url, key, raws);
        return ResponseEntity.ok().build();
    }

    private void fillModel(Map<String, Object> model) {
        model.put("isEnabled", carrierService.isEnabled());
        model.put("mockarooUrl", carrierService.getMockarooUrl());
        model.put("mockarooKey", carrierService.getMockarooKey());
        model.put("mockarooRows", carrierService.getMockarooRows());
        model.put("processingInterval", carrierService.getProcessingInterval());
        model.put("minInterval", MIN_INTERVAL);
        model.put("maxInterval", MAX_INTERVAL);
        model.put("minRows", MIN_COUNT_OF_ROWS);
        model.put("maxRows", MAX_COUNT_OF_ROWS);
    }

    private void fillModel(Map<String, Object> model, final List<String> errorList) {
        fillModel(model);
        model.put("errors", errorList);
    }

    private boolean isValidMockarooUrl(String mockarooUrl, String mockarooKey) {
        log.info("Validating Mockaroo URL and Key");
        try {
            final byte[] mockarooFileContent =
              mockarooClient.loadFile(mockarooUrl, mockarooKey, 1);
        } catch (Throwable e) {
            log.error("An error occurred while trying to read data from Mockaroo", e);
            return false;
        }
        log.info("Successful validation of Mockaroo URL and Key.");
        return true;
    }

    private void validateInputs(final boolean enabled, final int processingInterval, final int mockarooRows,
      final String mockarooUrl, final String mockarooKey, final List<String> errorList) {
        if (!(processingInterval >= MIN_INTERVAL && processingInterval <= MAX_INTERVAL)) {
            errorList.add(FIELD_NAME_PROCESSING_INTERVAL);
        }
        if (!(mockarooRows >= MIN_COUNT_OF_ROWS && mockarooRows <= MAX_COUNT_OF_ROWS)) {
            errorList.add(MOCAKAROO_ROWS);
        }
        if (enabled) {
            if (!isValidMockarooUrl(mockarooUrl, mockarooKey)) {
                errorList.add(MOCKAROO_URL_OR_AND_KEY);
            }
        }
    }
}
