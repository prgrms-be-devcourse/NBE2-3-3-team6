package com.redbox.global.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import lombok.Getter;
import lombok.Setter;

// ConfigurationProperties 자동 할당을 위한 Getter, Setter (Java Bean 규약에 따른 명명인 setXX 권장)
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public class SocialClientRegistration {

    private String clientId;
    private String clientSecret;

    public ClientRegistration naverClientRegistration() {

        String REDIRECT_URL = "http://localhost:8080/login/oauth2/code/naver";
        return ClientRegistration.withRegistrationId("naver")
                                 .clientId(clientId)
                                 .clientSecret(clientSecret)
                                 .redirectUri(REDIRECT_URL)
                                 .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                 .scope("name", "email")
                                 .authorizationUri("https://nid.naver.com/oauth2.0/authoreize")
                                 .tokenUri("https://nid.naver.com/oauth2.0/token")
                                 .userInfoUri("https://openapi.naver.com/v1/nid/me")
                                 .userNameAttributeName("response")
                                 .build();
    }
}