package com.telcaria.dcs.kibana.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak-admin")
public class KeycloakProperties {

    private String serverUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String username;
    private String password;
    private String usersRealm;

}