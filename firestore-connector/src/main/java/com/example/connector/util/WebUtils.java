package com.example.connector.util;

import com.example.connector.domain.ApiError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class WebUtils {

  public static void handleHttpClientErrors(HttpResponse<?> httpResponse) {
    HttpStatus httpStatus = HttpStatus.valueOf(httpResponse.statusCode());
    if (!httpStatus.is2xxSuccessful()) {
      throw new HttpClientErrorException(httpStatus, httpResponse.body().toString());
    }
  }

  public static byte[] buildApiError(
      ObjectMapper objectMapper,
      HttpStatus httpStatus,
      Throwable throwable
  ) {
    try {
      return
          objectMapper.writeValueAsBytes(
              buildApiError(
                  httpStatus,
                  throwable
              )
          );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static ApiError buildApiError(
      HttpStatus httpStatus,
      Throwable throwable
  ) {
    return
        ApiError.builder()
            .status(httpStatus)
            .message(throwable.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
  }
}
