package com.telcaria.dcs.keycloak_nginx;

import com.telcaria.dcs.authentication.service.KeycloakAuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
class KeycloakNginxTests {

  @Autowired
  KeycloakAuthenticationService keycloakAuthenticationService;

  @Test
  void modifyNginxConfigTest() {
    // /home/telcaria/nginx_example - content:
    // server {
    //    listen 25601;
    //    location / {
    //        proxy_pass http://10.5.7.12:5601;
    //        proxy_set_header  Host $host;
    //        proxy_set_header  X-Real-IP $remote_addr;
    //        proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
    //        proxy_set_header  Authorization "Bearer <token>";
    //    }
    //}
    log.info("Starting test. File located at /home/telcaria/nginx_example");

    // Read token from file
    String token = keycloakAuthenticationService.extractTokenFromFile();

    // Check if the token is valid
    if(!keycloakAuthenticationService.checkIfTokenIsActive(token)) {
      log.info("Token not valid, generating a new one");
      // Change token
      String newToken = keycloakAuthenticationService.requestToken();
      keycloakAuthenticationService.updateTokenInFile(token, newToken);
    } else {
      log.info("Token valid");
    }
  }

  @Test
  void manipulateTokenResponse() {
    String response = "{\"access_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJQS1Q1Q3Y3cVNwejIyYUJ1TXJmQVhNWVdBcUJfNkplaFlncElLR3V5VkM0In0.eyJqdGkiOiI1ODZjZTEwMi00NTBmLTQxN2YtOTU3ZS0zNDQ0OTZmYWI2M2UiLCJleHAiOjE2MDc5NDI3NzksIm5iZiI6MCwiaWF0IjoxNjA3OTM5MTc5LCJpc3MiOiJodHRwOi8vMTAuNS43LjExOjgwODAvYXV0aC9yZWFsbXMvNWdldmUiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNjlmYzZmY2YtYzliNS00ODlmLTk3MTQtZDU4NDIzMjZiNDRjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoia2liYW5hIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiODE3MWJlM2ItMjE0Zi00ZWVkLWJlMGQtMWMzZDY4ZDMyNDdkIiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vMTAuNS43LjEyOjU2MDEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJraWJhbmEiOnsicm9sZXMiOlsidmlldy1zZWFyY2hlcyIsImRpc2NvdmVyIiwidmlldy1kYXNoYm9hcmRzIiwibWFuYWdlLXZpc3VhbGl6YXRpb25zIiwidW1hX3Byb3RlY3Rpb24iLCJ2aWV3LXZpc3VhbGl6YXRpb25zIiwibWFuYWdlLWRhc2hib2FyZHMiLCJtYW5hZ2Uta2liYW5hIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJncm91cHMiOltdLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJraWJhbmF1c2VyIn0.H9ircn69APxZxOsSOeAqwO6NjcHGL3wTQ83DqekRsc1i2StXcw3RTwxBOkh5Zy8jaQzXqws0vPUEpFRF2582l1CwFonfhPpBugyzzNjdLVyQ7InuiAvKbGF5Xxi5mSPMm_S9JUWHH6r8z_KujIl_mqRx1kVLeTI0HsXINDbqtxkfu0OrjTlxONzTPbXgywbb-5mRM8hrcMUJsnlQA26LoVXBwumiPdUORQzn4HFU06zI0MYyMSeCgDAfJpwX_rwnV9Bqyb95HHFOUDXzl7zHYLcQ-Un5_96cVBsppH-wIH1V7m6zKwp0XWS6bMuSlYapeQJ3xdbPI0DLrbgGrgYaXw\",\"expires_in\":3600,\"refresh_expires_in\":1800,\"refresh_token\":\"eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI1YTc1MDk5OC04NjQ2LTQwODItODhhMS1iZDg2NmQyZGZiYzcifQ.eyJqdGkiOiI3OTA5OGJkOC1kNjViLTQyYWItODFiOS0yYTkxNzRjYjcxMzAiLCJleHAiOjE2MDc5NDA5NzksIm5iZiI6MCwiaWF0IjoxNjA3OTM5MTc5LCJpc3MiOiJodHRwOi8vMTAuNS43LjExOjgwODAvYXV0aC9yZWFsbXMvNWdldmUiLCJhdWQiOiJodHRwOi8vMTAuNS43LjExOjgwODAvYXV0aC9yZWFsbXMvNWdldmUiLCJzdWIiOiI2OWZjNmZjZi1jOWI1LTQ4OWYtOTcxNC1kNTg0MjMyNmI0NGMiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoia2liYW5hIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiODE3MWJlM2ItMjE0Zi00ZWVkLWJlMGQtMWMzZDY4ZDMyNDdkIiwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJraWJhbmEiOnsicm9sZXMiOlsidmlldy1zZWFyY2hlcyIsImRpc2NvdmVyIiwidmlldy1kYXNoYm9hcmRzIiwibWFuYWdlLXZpc3VhbGl6YXRpb25zIiwidW1hX3Byb3RlY3Rpb24iLCJ2aWV3LXZpc3VhbGl6YXRpb25zIiwibWFuYWdlLWRhc2hib2FyZHMiLCJtYW5hZ2Uta2liYW5hIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBlbWFpbCBwcm9maWxlIn0.q6LzJZK7fp8akwvRazRB6FVL_OXiXr86mBT0ddiiJ7U\",\"token_type\":\"bearer\",\"id_token\":\"eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJQS1Q1Q3Y3cVNwejIyYUJ1TXJmQVhNWVdBcUJfNkplaFlncElLR3V5VkM0In0.eyJqdGkiOiJkY2Y1ODgyMC00NzNlLTQyNTUtOWE4NC1lODA4YTkxOTFmNTciLCJleHAiOjE2MDc5NDI3NzksIm5iZiI6MCwiaWF0IjoxNjA3OTM5MTc5LCJpc3MiOiJodHRwOi8vMTAuNS43LjExOjgwODAvYXV0aC9yZWFsbXMvNWdldmUiLCJhdWQiOiJraWJhbmEiLCJzdWIiOiI2OWZjNmZjZi1jOWI1LTQ4OWYtOTcxNC1kNTg0MjMyNmI0NGMiLCJ0eXAiOiJJRCIsImF6cCI6ImtpYmFuYSIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6IjgxNzFiZTNiLTIxNGYtNGVlZC1iZTBkLTFjM2Q2OGQzMjQ3ZCIsImFjciI6IjEiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImdyb3VwcyI6W10sInByZWZlcnJlZF91c2VybmFtZSI6ImtpYmFuYXVzZXIifQ.HAT4C55JYuiJWnC9hL3HjQZVmbyvMg_9XBX_Sh_ErGcoDVg-N1e_aU_lEHFfYwl3NcP7Lg_49cIth8svE9yskPi5325Pu7IiAd4EgyJSc37MO04ipUfqG2SZKig0DIRL6WIR84FmN1PBtPPWPkYgRqU5NI1nvc0CYS14UWThtUesnUjvQDKRlNcosUxxWt6_-Q5V9rKGD61q5SGAC-Z8EuX1la0iuf8CkPlVx9qPrEGcyOnIKZ4r9EoBeL7AtkUa5NgAu8ZbeCdaMMc_SJxtFEP0g94bUA5-a8FO0VrV1v59Q7XSA6vyFpo0W58d3CujeVsHy67xnhZeYmfHbWL9tw\",\"not-before-policy\":0,\"session_state\":\"8171be3b-214f-4eed-be0d-1c3d68d3247d\",\"scope\":\"openid email profile\"}";

    String[] parts = response.split(",");
    boolean found = false;
    int i = 0;

    while (!found && i < parts.length) {

      String part = parts[i];
      log.info(part);

      if (part.indexOf("access_token") != -1) {
        log.info("FOUND");
        found = true;

        String token = part.split(":")[1];
        token = token.replaceAll("\"", "");
        log.info("TOKEN: " + token);
      }

      i++;
    }


  }
}

