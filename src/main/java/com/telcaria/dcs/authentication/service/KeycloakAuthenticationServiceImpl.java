package com.telcaria.dcs.authentication.service;

import com.telcaria.dcs.authentication.client.KeycloakAuthenticationClient;
import com.telcaria.dcs.authentication.wrapper.IntrospectTokenRequest;
import com.telcaria.dcs.authentication.wrapper.TokenRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@Service
@Slf4j
@Transactional
public class KeycloakAuthenticationServiceImpl implements KeycloakAuthenticationService {

    private KeycloakAuthenticationClient keycloakAuthenticationClient;
    private Environment env;

    @Autowired
    public KeycloakAuthenticationServiceImpl(KeycloakAuthenticationClient keycloakAuthenticationClient, Environment env) {
        this.keycloakAuthenticationClient = keycloakAuthenticationClient;
        this.env = env;
    }

    @Override
    public String extractTokenFromFile() {
        String filepath = env.getProperty("nginx-file");
        String token = "";
        int n = 7; // The line number
        try {
            String line = Files.readAllLines(Paths.get(filepath)).get(n);
            String[] parts = line.split(" ");
            String part = parts[12];
            token = part.split("\"")[0];
            log.info("Token extracted: " + token);
        }
        catch(IOException e){
            log.error(e.toString());
        }

        return token;
    }

    @Override
    public boolean checkIfTokenIsActive(String token) {
        IntrospectTokenRequest introspectTokenRequest = new IntrospectTokenRequest();
        introspectTokenRequest.setTokenTypeHint("requesting_party_token");
        introspectTokenRequest.setToken(token);

        log.info("Introspect token: " + introspectTokenRequest.toString());

        String auth = "Basic " + buildBasicAuth(env.getProperty("kibana.client-id"),
                                                env.getProperty("kibana.client-secret"));

        return keycloakAuthenticationClient.checkIfTokenIsActive(auth, introspectTokenRequest);
    }

    @Override
    public String obtainUserFromToken(String token) {
        IntrospectTokenRequest introspectTokenRequest = new IntrospectTokenRequest();
        introspectTokenRequest.setTokenTypeHint("requesting_party_token");
        introspectTokenRequest.setToken(token);

        //log.info("Introspect token: " + introspectTokenRequest.toString());

        String auth = "Basic " + buildBasicAuth(env.getProperty("kibana.client-id"),
                                                env.getProperty("kibana.client-secret"));

        return keycloakAuthenticationClient.obtainUserFromToken(auth, introspectTokenRequest);
    }

    @Override
    public String requestToken() {

        String token = "";

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setGrantType("password");
        tokenRequest.setUsername(env.getProperty("keycloak-user"));
        tokenRequest.setPassword(env.getProperty("keycloak-password"));
        tokenRequest.setScope("openid");
        tokenRequest.setClientId(env.getProperty("kibana.client-id"));

        log.info("Request token: " + tokenRequest.toString());

        String auth = "Basic " + buildBasicAuth(env.getProperty("kibana.client-id"),
                                                env.getProperty("kibana.client-secret"));

        try {
            token = keycloakAuthenticationClient.requestToken(auth, tokenRequest);
            log.info("Token generated: " + token);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("Token not generated correctly");
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public void updateTokenInFile(String oldToken, String newToken) {
        String filepath = env.getProperty("nginx-file");

        log.info("Updating nginx file");
        Scanner sc = null;
        try {
            sc = new Scanner(new File(filepath));

            StringBuffer buffer = new StringBuffer();
            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (nextLine.indexOf("Authorization") != -1) {
                    log.info("Original line: " + nextLine);
                    nextLine = nextLine.replaceAll(oldToken, newToken);
                    log.info("Line changed: " + nextLine);
                }
                nextLine = nextLine + System.lineSeparator();
                buffer.append(nextLine);
            }

            sc.close();

            FileWriter writer = null;
            try {
                writer = new FileWriter(filepath);
                writer.append(buffer.toString());
                writer.flush();
                writer.close();

                // Finally, update nginx configuration.

                String s;
                Process p;
                try {
                    p = Runtime.getRuntime().exec("sudo /etc/init.d/nginx reload");
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
                    log.info("Updating nginx...");
                    while ((s = br.readLine()) != null) {
                        log.info(s);
                    }
                    p.waitFor();
                    log.info("Update nginx exit: " + p.exitValue());
                    p.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String buildBasicAuth(String user, String password) {
        String authString = user + ":" + password;
        byte[] authEncBytes = Base64Utils.encode(authString.getBytes());
        return new String(authEncBytes);
    }
}
