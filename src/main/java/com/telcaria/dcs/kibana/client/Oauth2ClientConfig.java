/*
package com.telcaria.dcs.kibana.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class Oauth2ClientConfig {
    @Value("${kibana.oauth-enabled}")
    boolean useOauth;

    @Bean
    ReactiveClientRegistrationRepository getRegistration(
            @Value("${spring.security.oauth2.client.provider.kibana.token-uri}") String tokenUri,
            @Value("${spring.security.oauth2.client.registration.kibana.client-id}") String clientId,
            @Value("${spring.security.oauth2.client.registration.kibana.client-secret}") String clientSecret) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("kibana")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scope("openid")
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean
    WebClient webClient(ReactiveClientRegistrationRepository clientRegistrations) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(
                        clientRegistrations,
                        new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
        oauth.setDefaultClientRegistrationId("kibana");

        WebClient.Builder builder = WebClient.builder();

        if(useOauth){
            builder.filter(oauth);
        }
        return builder.build();
    }
}
*/
