package com.drohobytskyy.gcs.validator;

import java.util.List;

public interface RegexValidator {

    boolean validate(String str, List<String> regexSet);

}
