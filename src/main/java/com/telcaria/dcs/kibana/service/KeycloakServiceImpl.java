package com.telcaria.dcs.kibana.service;

import com.telcaria.dcs.kibana.client.KeycloakProperties;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@Transactional
public class KeycloakServiceImpl implements KeycloakService{

    KeycloakProperties keycloakProperties;

    @Autowired
    public KeycloakServiceImpl(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Override
    public String getUsersByUseCase(String useCase){
        String userString = "";
        Keycloak keycloak = Keycloak.getInstance(
                keycloakProperties.getServerUrl(),
                keycloakProperties.getRealm(),
                keycloakProperties.getUsername(),
                keycloakProperties.getPassword(),
                keycloakProperties.getClientId());

        RealmResource realm = keycloak.realm(keycloakProperties.getUsersRealm());
        for(UserRepresentation user: realm.users().list()){
            if(user.getAttributes() != null  &&
                    user.getAttributes().containsKey("use_cases") &&
                    user.getAttributes().get("use_cases").contains(useCase)) {
                    userString = userString.concat(user.getUsername() + ",");
            }
        }

        // Add generic user
        userString = userString.concat("kibanauser");

        return userString;
    }

}
