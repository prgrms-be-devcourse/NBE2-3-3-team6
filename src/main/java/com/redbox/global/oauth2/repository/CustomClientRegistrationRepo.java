package com.redbox.global.oauth2.repository;

import com.redbox.global.oauth2.SocialClientRegistration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

@Configuration
public class CustomClientRegistrationRepo {

    private final SocialClientRegistration socialClientRegistration;

    public CustomClientRegistrationRepo(SocialClientRegistration socialClientRegistration) {
        this.socialClientRegistration = socialClientRegistration;
    }

    public ClientRegistrationRepository clientRegistrationRepository() {
        // 현재 종류가1개, 많아봐야 3~5개 이기 때문에 InMemory 로 처리
        return new InMemoryClientRegistrationRepository(socialClientRegistration.naverClientRegistration());
    }
}
