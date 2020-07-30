package com.telcaria.dcs.kibana.client;


import com.telcaria.dcs.kibana.service.KibanaService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Arrays;
import java.util.Base64;

@Service
@Slf4j
@Transactional
public class KibanaConnectorServiceImpl implements KibanaConnectorService {

    @Autowired
    private KibanaProperties kibanaProperties;
    //@Autowired
    private WebClient client;

    public boolean postKibanaObject(String kibanaJsonObject) {

        log.debug("Kibana object:\n{}", kibanaJsonObject);
        log.debug("Requesting new object to Kibana");
        try {
            client = WebClient.create(kibanaProperties.getBaseUrl());
            String response = client.post()
                    .uri("/api/kibana/dashboards/import")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue(kibanaJsonObject)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
                log.error("Error while trying to create in Kibana. " + e.getResponseBodyAsString());
                return false;
        }
    }

    public boolean postKibanaIndexPattern(String indexPattern) {

        log.debug("Requesting new object to Kibana");
        try {
            client = WebClient.create(kibanaProperties.getBaseUrl());
            String response = client.post()
                    .uri("/api/saved_objects/index-pattern/" + indexPattern)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\n" +
                            "  \"attributes\": {\n" +
                            "    \"title\": \"" + indexPattern + "\"\n" +
                            "  }\n" +
                            "}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to create in Kibana. " + e.getResponseBodyAsString());
            return false;
        }
    }

    public boolean setOwner(String dashboardId, String user) {
        //The user can be the userId, the username or the email depending on the configuration given to the kibana keycloak plugin

        log.debug("giveDashboardPermissionToUser {} {}", dashboardId, user);
        try {
            client = WebClient.create(kibanaProperties.getBaseUrl());
            String response = client.put()
                    .uri("/api/saved_objects/dashboard/" + dashboardId + "/permissions/view")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\"users\": [\"" + user + "\"]}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to create in Kibana. " + e.getResponseBodyAsString());
            return false;
        }
    }


    public boolean removeKibanaObject(String kibanaObjectId, String kibanaObjectType) {

        log.debug("Kibana object:\n{}", kibanaObjectId);
        log.debug("Removing new object to Kibana");
        try {
            client = WebClient.create(kibanaProperties.getBaseUrl());
            String response = client.delete()
                    .uri("/api/saved_objects/" + kibanaObjectType + "/" + kibanaObjectId)
                    .header("kbn-xsrf", "true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.debug("Object successfully Kibana. " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to remove in Kibana. " + e.getResponseBodyAsString());
            return false;
        }

    }
}
