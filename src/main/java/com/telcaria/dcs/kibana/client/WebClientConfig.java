package com.telcaria.dcs.kibana.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.UnAuthenticatedServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.AuthenticationMethod;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {


    @Bean("cr")
    ReactiveClientRegistrationRepository getRegistration(
//            @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String tokenUri,
//            @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String clientId,
//            @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String clientSecret
           @Value("${kibana.token-uri}") String tokenUri,
            @Value("${kibana.client-id}") String clientId,
            @Value("${kibana.client-secret}") String clientSecret
    ) {
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("keycloak")
                .tokenUri(tokenUri)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .build();
        return new InMemoryReactiveClientRegistrationRepository(registration);
    }

    @Bean(name = "keycloak")
    WebClient webClient(@Qualifier("cr") ReactiveClientRegistrationRepository clientRegistrations) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations,
                        new UnAuthenticatedServerOAuth2AuthorizedClientRepository());
        oauth.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(oauth)
                .build();
    }

}