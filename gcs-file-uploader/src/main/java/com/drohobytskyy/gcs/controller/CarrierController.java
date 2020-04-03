package com.drohobytskyy.gcs.controller;

import com.drohobytskyy.gcs.service.CarrierService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class CarrierController {

    public static final String FIELD_NAME_SUPPOSED_INTERVAL = "supposedInterval";
    public static final int MIN_INTERVAL = 1;
    public static final int MAX_INTERVAL = 100;
    private final CarrierService carrier;

    @Autowired
    public CarrierController(final CarrierService carrier) {
        this.carrier = carrier;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView homePage(final Map<String, Object> model) {
        return new ModelAndView("home", carrier.homePage(model));
    }

    @RequestMapping(path = "/", method = RequestMethod.POST)
    public ModelAndView buttonPush(
      @RequestParam(defaultValue = "false") final boolean isSupposedToWork,
      @RequestParam(defaultValue = "0") final int supposedInterval,
      final Map<String, Object> model) {
        if (supposedInterval >= MIN_INTERVAL && supposedInterval <= MAX_INTERVAL) {
            return new ModelAndView("home", carrier.processButtonPush(isSupposedToWork, supposedInterval, model));
        } else {
            model.put("errors", FIELD_NAME_SUPPOSED_INTERVAL);
            return new ModelAndView("home", model);
        }
    }

    // curl -X POST http://localhost:5757/gcs
    @RequestMapping(path = "/gcs", method = RequestMethod.POST)
    public String processRequest() {
        return carrier.processRequest();
    }

}
