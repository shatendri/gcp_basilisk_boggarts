package com.drohobytskyy.gcs.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class MockarooParamsContainer {

    @Value("${mockaroo.url}")
    private String url;

    @Value("${mockaroo.key}")
    private String key;

    @Value("${mockaroo.timeout}")
    private int timeout;

}
