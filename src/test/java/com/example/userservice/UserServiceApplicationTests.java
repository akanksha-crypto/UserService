package com.example.userservice;

import com.example.userservice.security.repositories.ClientRepository;
import com.example.userservice.security.services.JpaRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.UUID;

@SpringBootTest
class UserServiceApplicationTests {
    @Autowired
    private JpaRegisteredClientRepository jpaRegisteredClientRepository;
    private ClientRepository clientRepository;
    @Test
    void contextLoads() {
    }

    @Test
    void registerClientInDB(){
        RegisteredClient oidcClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("sampleclient")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("https://oauth.pstmn.io/v1/callback")
//                .scope(OidcScopes.OPENID)
//                .scope(OidcScopes.PROFILE)
                .scope("ADMIN")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
        jpaRegisteredClientRepository.save(oidcClient);
    }
}
