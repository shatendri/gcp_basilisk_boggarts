package com.drohobytskyy.gcs.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileNameGenerator {

    public String generateFileName() {
        log.info("Generating a name for a file which was downloaded from Mockaroo.");
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd_hh:mm:ss");
        String strDate = dateFormat.format(date);
        return "Mockaroo_"
          .concat(strDate)
          .concat("_") // smile
          .concat(UUID.randomUUID()
            .toString()
            .substring(0, 8))
          .concat(".csv");
    }
}
