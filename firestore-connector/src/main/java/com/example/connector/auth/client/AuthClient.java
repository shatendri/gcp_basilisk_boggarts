package com.example.connector.auth.client;

import com.example.connector.auth.domain.OktaAuthorizationUrlResponse;
import com.example.connector.auth.domain.OktaOAuth2TokenResponse;

public interface AuthClient {

  OktaAuthorizationUrlResponse buildAuthorizationUrl();

  OktaOAuth2TokenResponse obtainOktaOAuth2TokenByCode(String code) throws Exception;
}
