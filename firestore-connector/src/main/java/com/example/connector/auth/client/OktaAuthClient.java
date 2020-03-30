package com.example.connector.auth.client;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.example.connector.auth.domain.OktaAuthorizationUrlResponse;
import com.example.connector.auth.domain.OktaOAuth2TokenResponse;
import com.example.connector.util.WebUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationResponseType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OktaAuthClient implements AuthClient {

  private final HttpClient oAuth2HttpClient;
  private final ObjectMapper objectMapper;
  private final String oktaOauth2ClientId;
  private final String oktaOauth2ClientSecret;
  private final String oktaOauth2Issuer;
  private final String oktaOauth2TokenPath;
  private final String oktaOauth2TokenRedirectUri;
  private final String oktaOauth2AuthorizePath;

  public OktaAuthClient(
      HttpClient oAuth2HttpClient,
      ObjectMapper objectMapper,
      @Value("${okta.oauth2.client-id}") final String oktaOauth2ClientId,
      @Value("${okta.oauth2.client-secret}") final String oktaOauth2ClientSecret,
      @Value("${okta.oauth2.issuer}") final String oktaOauth2Issuer,
      @Value("${okta.oauth2.token-path}") final String oktaOauth2TokenPath,
      @Value("${okta.oauth2.token-redirect-uri}") final String oktaOauth2TokenRedirectUri,
      @Value("${okta.oauth2.authorize-path}") final String oktaOauth2AuthorizePath
  ) {
    this.oAuth2HttpClient = oAuth2HttpClient;
    this.objectMapper = objectMapper;
    this.oktaOauth2ClientId = oktaOauth2ClientId;
    this.oktaOauth2ClientSecret = oktaOauth2ClientSecret;
    this.oktaOauth2Issuer = oktaOauth2Issuer;
    this.oktaOauth2TokenPath = oktaOauth2TokenPath;
    this.oktaOauth2TokenRedirectUri = oktaOauth2TokenRedirectUri;
    this.oktaOauth2AuthorizePath = oktaOauth2AuthorizePath;
  }

  @Override
  public OktaAuthorizationUrlResponse buildAuthorizationUrl() {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("client_id", oktaOauth2ClientId);
    params.add("response_type", OAuth2AuthorizationResponseType.CODE.getValue());
    params.add("scope", OidcScopes.OPENID);
    params.add("redirect_uri", oktaOauth2TokenRedirectUri);
    params.add("state", UUID.randomUUID().toString());

    String url =
        UriComponentsBuilder.fromHttpUrl(oktaOauth2Issuer)
            .path(oktaOauth2AuthorizePath)
            .queryParams(params)
            .encode(UTF_8)
            .build()
            .toString();

    return
        OktaAuthorizationUrlResponse.builder()
            .url(url)
            .build();
  }

  @Override
  public OktaOAuth2TokenResponse obtainOktaOAuth2TokenByCode(String code) throws Exception {

    HttpRequest httpRequest =
        HttpRequest.newBuilder()
            .POST(constructOAuth2TokenRequestBody(code))
            .uri(new URI(oktaOauth2Issuer + oktaOauth2TokenPath))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();

    HttpResponse<String> httpResponse =
        oAuth2HttpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

    WebUtils.handleHttpClientErrors(httpResponse);

    return
        objectMapper.readValue(
            httpResponse.body().getBytes(UTF_8),
            OktaOAuth2TokenResponse.class
        );
  }

  private HttpRequest.BodyPublisher constructOAuth2TokenRequestBody(String code) {
    Map<String, String> params = new HashMap<>();
    params.put("client_id", oktaOauth2ClientId);
    params.put("client_secret", oktaOauth2ClientSecret);
    params.put("grant_type", AuthorizationGrantType.AUTHORIZATION_CODE.getValue());
    params.put("redirect_uri", oktaOauth2TokenRedirectUri);
    params.put("code", code);

    String body =
        params.entrySet()
            .stream()
            .map(e -> constructParam(e.getKey(), e.getValue()))
            .collect(Collectors.joining("&"));

    return HttpRequest.BodyPublishers.ofString(body);
  }

  private String constructParam(String key, String value) {
    return encode(key, UTF_8) + "=" + encode(value, UTF_8);
  }
}
