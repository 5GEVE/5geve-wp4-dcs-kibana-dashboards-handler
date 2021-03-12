package com.telcaria.dcs.authentication.client;

import com.telcaria.dcs.authentication.wrapper.IntrospectTokenRequest;
import com.telcaria.dcs.authentication.wrapper.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@Transactional
public class KeycloakAuthenticationClientImpl implements KeycloakAuthenticationClient {

  private Environment env;

  @Autowired
  public KeycloakAuthenticationClientImpl(Environment env) {
    this.env = env;
  }

  public boolean checkIfTokenIsActive(String authorizationHeader, IntrospectTokenRequest introspectTokenRequest) {

    boolean valid = false;
    String response = introspectToken(authorizationHeader, introspectTokenRequest);

    if (response.indexOf("\"active\":true") != -1) {
      valid = true;
    }

    return valid;
  }

  @Override
  public String obtainUserFromToken(String authorizationHeader, IntrospectTokenRequest introspectTokenRequest) {

    String response = introspectToken(authorizationHeader, introspectTokenRequest);
    String[] fields = response.split(",");

    String user = "";
    boolean found = false;
    int i = 0;

    while (!found && i < fields.length) {
      if (fields[i].indexOf("\"username\":") != -1) {
        found = true;
        user = fields[i].split("\"")[3];
      } else {
        i++;
      }
    }

    return user;
  }

  private String introspectToken(String authorizationHeader, IntrospectTokenRequest introspectTokenRequest) {

    String responseString = "";

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    //RequestBody body = RequestBody.create("token_type_hint=" + introspectTokenRequest.getTokenTypeHint()
    //                                              + "&token=" + introspectTokenRequest.getToken(),
    //                                      mediaType);
    RequestBody body = RequestBody.create("token=" + introspectTokenRequest.getToken(), mediaType);
    Request request = new Request.Builder()
            .url(env.getProperty("kibana.token-uri") + "/introspect")
            .method("POST", body)
            .addHeader("Authorization", authorizationHeader)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
    try {
      Response response = client.newCall(request).execute();

      if (response.isSuccessful()) {
        responseString = response.body().string();
      } else {
        log.warn("Introspect Token operation without successful result");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return responseString;
  }

  public String requestToken(String authorizationHeader, TokenRequest tokenRequest) throws IOException, Exception {

    String token = "";

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
    RequestBody body = RequestBody.create("grant_type=" + tokenRequest.getGrantType()
                                                  + "&username=" + tokenRequest.getUsername()
                                                  + "&password=" + tokenRequest.getPassword()
                                                  + "&scope=" + tokenRequest.getScope()
                                                  + "&client-id=" + tokenRequest.getClientId(),
                                          mediaType);
    Request request = new Request.Builder()
            .url(env.getProperty("kibana.token-uri"))
            .method("POST", body)
            .addHeader("Authorization", authorizationHeader)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();
    Response response = client.newCall(request).execute();

    if (response.isSuccessful()) {
      String responseString = response.body().string();

      String[] parts = responseString.split(",");
      boolean found = false;
      int i = 0;

      while (!found && i < parts.length) {

        String part = parts[i];

        if (part.indexOf("access_token") != -1) {
          found = true;

          token = part.split(":")[1];
          token = token.replaceAll("\"", "");
        }

        i++;
      }
      if (!found) {
        throw new Exception ("Token created but not found");
      }
    } else {
      throw new Exception("Token not created");
    }

    return token;
  }
}