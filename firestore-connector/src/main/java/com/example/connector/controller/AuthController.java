package com.example.connector.controller;

import com.example.connector.auth.client.AuthClient;
import com.example.connector.auth.domain.OktaAuthorizationUrlResponse;
import com.example.connector.auth.domain.OktaOAuth2TokenResponse;
import java.security.Principal;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {

  private final AuthClient oktaAuthClient;

  public AuthController(AuthClient oktaAuthClient) {
    this.oktaAuthClient = oktaAuthClient;
  }

  //  @PreAuthorize("hasAuthority('test')")
  @GetMapping("/authorization-code/callback")
  public Mono<OktaOAuth2TokenResponse> getOktaOAuth2Token(
      @RequestParam String code,
      Principal principal
  ) throws Exception {

    OktaOAuth2TokenResponse token =
        oktaAuthClient.obtainOktaOAuth2TokenByCode(code);

    return Mono.justOrEmpty(token);
  }

  @GetMapping("/authorization-url")
  public Mono<OktaAuthorizationUrlResponse> getAuthorizationUrl(Principal principal) {
    return Mono.justOrEmpty(oktaAuthClient.buildAuthorizationUrl());
  }
}
