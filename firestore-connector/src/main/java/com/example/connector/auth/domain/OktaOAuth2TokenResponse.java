package com.example.connector.auth.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OktaOAuth2TokenResponse {

  @JsonProperty("token_type")
  private String tokenType;
  @JsonProperty("expires_in")
  private Integer expiresIn;
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("scope")
  private String scope;
  @JsonProperty("id_token")
  private String idToken;
}
