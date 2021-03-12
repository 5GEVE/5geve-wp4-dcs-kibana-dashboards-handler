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

    private KibanaProperties kibanaProperties;
    private WebClient client;

    @Autowired
    public KibanaConnectorServiceImpl(KibanaProperties kibanaProperties, WebClient client) {
        this.kibanaProperties = kibanaProperties;
        this.client = client;
    }

    public boolean postKibanaObject(String kibanaJsonObject) {

        log.debug("Kibana object:\n{}", kibanaJsonObject);
        log.debug("Requesting new object to Kibana");
        try {
            String response = client.post()
                    .uri(kibanaProperties.getBaseUrl() + "/api/kibana/dashboards/import")
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

    public boolean putKibanaIndexPattern(String indexPattern) {

        log.debug("Requesting new object to Kibana");
        try {
            int tries = 0;
            String indexPatternFields;
            do {
                log.debug("Requesting new object to Kibana. Try n {}", tries);
                indexPatternFields = getIndexPatternFields(indexPattern);
                tries ++;
                if (indexPatternFields == null) {
                    Thread.sleep(5000);
                }
            } while (indexPatternFields == null && tries < 3);

            assert indexPatternFields != null;
            indexPatternFields = indexPatternFields.substring(10, indexPatternFields.length()-1).replaceAll("\"", "\\\\\"");

            String response = client.put()
                    .uri(kibanaProperties.getBaseUrl() + "/api/saved_objects/index-pattern/index_" + indexPattern)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\"attributes\":{\"title\":\"" + indexPattern + "\",\"fields\":\"" + indexPatternFields + "\"}}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("Object successfully created. Response = " + response);
            return true;
        } catch (WebClientResponseException | InterruptedException e) {
            log.error("Error while trying to create in Kibana. " + e);
            return false;
        }
    }

    public String getIndexPatternFields(String indexPattern){
        //http://10.9.8.202:5601/api/index_patterns/_fields_for_wildcard?pattern=uc.4.france_nice.infrastructure_metric.expb_metricid&meta_fields=_source&meta_fields=_id&meta_fields=_type&meta_fields=_index&meta_fields=_score
        log.debug("getIndexPatternFields {}", indexPattern);
        try {
            String response = client.get()
                    .uri(kibanaProperties.getBaseUrl() + "/api/index_patterns/_fields_for_wildcard?pattern=" + indexPattern + "&meta_fields=_source&meta_fields=_id&meta_fields=_type&meta_fields=_index&meta_fields=_score")
                    .accept(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            assert response.contains("fields");
            log.debug("getIndexPatternFields. Response = " + response);
            return response;
        } catch (WebClientResponseException e) {
            log.error("Error while getIndexPatternFields " + e.getResponseBodyAsString());
            return null;
        }

    }

    public boolean setOwner(String dashboardId, String user) {
        //The user can be the userId, the username or the email depending on the configuration given to the kibana keycloak plugin

        log.debug("setOwner {} {}", dashboardId, user);
        try {
            String response = client.put()
                    .uri(kibanaProperties.getBaseUrl() + "/api/saved_objects/dashboard/" + dashboardId + "/permissions/view")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("kbn-xsrf", "true")
                    .bodyValue("{\"users\": " + getStringUsers(user) + "}")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.debug("setOwner successfully. Response = " + response);
            return true;
        } catch (WebClientResponseException e) {
            log.error("Error while trying to setOwner in Kibana. " + e.getResponseBodyAsString());
            return false;
        }
    }

    private String getStringUsers(String users){
        String[] usersArray = users.split(",");
        String generatedString = "[";

        for(int i = 0; i < usersArray.length; i++){
            generatedString = generatedString.concat("\"" + usersArray[i] + "\"");
            if (i < usersArray.length - 1){
                generatedString = generatedString.concat(",");
            }
        }
        generatedString = generatedString.concat("]");

        return  generatedString;
    }

    public boolean removeKibanaObject(String kibanaObjectId, String kibanaObjectType) {

        log.debug("Kibana object:\n{}", kibanaObjectId);
        log.debug("Removing new object to Kibana");
        try {
            String response = client.delete()
                    .uri(kibanaProperties.getBaseUrl() + "/api/saved_objects/" + kibanaObjectType + "/" + kibanaObjectId)
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
