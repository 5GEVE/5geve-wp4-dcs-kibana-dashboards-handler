package com.telcaria.dcs.kibana.client;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kibana")
public class KibanaProperties {

    private String baseUrl;
    private String dashboardUrl;
    //private String username;
    //private String password;
    private boolean dashboardOwnerEnabled;

}