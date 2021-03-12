package com.telcaria.dcs.authentication.client;

import com.telcaria.dcs.authentication.wrapper.IntrospectTokenRequest;
import com.telcaria.dcs.authentication.wrapper.TokenRequest;

import java.io.IOException;

public interface KeycloakAuthenticationClient {

  boolean checkIfTokenIsActive(String authorizationHeader, IntrospectTokenRequest introspectTokenRequest);
  String obtainUserFromToken(String authorizationHeader, IntrospectTokenRequest introspectTokenRequest);
  String requestToken(String authorizationHeader, TokenRequest tokenRequest) throws IOException, Exception;
}