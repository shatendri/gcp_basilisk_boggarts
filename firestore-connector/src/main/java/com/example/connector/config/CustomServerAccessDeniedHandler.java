package com.example.connector.config;

import com.example.connector.util.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class CustomServerAccessDeniedHandler implements ServerAccessDeniedHandler {

  private final ObjectMapper objectMapper;

  public CustomServerAccessDeniedHandler(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception) {
    return
        Mono.defer(() -> Mono.just(exchange.getResponse()))
            .flatMap((response) -> buildResponse(response, exception));
  }

  private Mono<Void> buildResponse(ServerHttpResponse response, AccessDeniedException exception) {
    HttpStatus httpStatus = HttpStatus.FORBIDDEN;
    response.setStatusCode(httpStatus);
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] apiErrorAsBytes = WebUtils.buildApiError(objectMapper, httpStatus, exception);
    DataBuffer buffer = response.bufferFactory().wrap(apiErrorAsBytes);

    return
        response.writeWith(Mono.just(buffer))
            .doOnError((error) -> DataBufferUtils.release(buffer));
  }
}
