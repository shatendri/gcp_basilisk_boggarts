package com.example.connector.util;

import java.net.http.HttpResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class WebUtils {

  public static void handleHttpClientErrors(HttpResponse<?> httpResponse) {
    HttpStatus httpStatus = HttpStatus.valueOf(httpResponse.statusCode());
    if (!httpStatus.is2xxSuccessful()) {
      throw new HttpClientErrorException(httpStatus, httpResponse.body().toString());
    }
  }
}
