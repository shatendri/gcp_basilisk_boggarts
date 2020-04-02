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

    final private CarrierService carrier;

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
      @RequestParam(defaultValue = "false") final String isSupposedToWork,
      @RequestParam(defaultValue = "0") final String supposedInterval,
      final Map<String, Object> model) {
        return new ModelAndView("home", carrier.processButtonPush(isSupposedToWork, supposedInterval, model));
    }

    // curl -X POST http://localhost:5757/gcs
    @RequestMapping(path = "/gcs", method = RequestMethod.POST)
    public String processRequest() {
        return carrier.processRequest();
    }

}
