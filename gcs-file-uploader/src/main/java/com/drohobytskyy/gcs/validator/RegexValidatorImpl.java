package com.drohobytskyy.gcs.validator;

import java.util.List;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@AllArgsConstructor
@Component
public class RegexValidatorImpl implements RegexValidator {

    @Override
    public boolean validate(
      final String str,
      final List<String> regexSet) {

        for (final String regex : regexSet) {
            final Pattern pattern = Pattern.compile(regex);
            if (!pattern.matcher(str).matches()) {
                return false;
            }
        }

        return true;
    }
}
