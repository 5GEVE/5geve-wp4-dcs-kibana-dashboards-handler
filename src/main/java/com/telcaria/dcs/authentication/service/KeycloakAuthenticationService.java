package com.telcaria.dcs.authentication.service;

public interface KeycloakAuthenticationService {

    String extractTokenFromFile();
    boolean checkIfTokenIsActive(String token);
    String obtainUserFromToken(String token);
    String requestToken();
    void updateTokenInFile(String oldToken, String newToken);

}