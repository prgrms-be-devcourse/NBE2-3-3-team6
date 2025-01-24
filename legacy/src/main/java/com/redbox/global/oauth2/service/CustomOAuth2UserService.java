package com.redbox.global.oauth2.service;

import com.redbox.domain.user.entity.RoleType;
import com.redbox.domain.user.entity.Status;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.global.oauth2.dto.CustomOAuth2User;
import com.redbox.global.oauth2.dto.NaverResponse;
import com.redbox.global.oauth2.dto.OAuth2Response;
import com.redbox.global.oauth2.dto.SocialUserDTO;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(">>> [ " + oAuth2User + " ] <<<");

        // login 요청이 어디서 온지 (네이버, 구글,  ...)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else {
            return null;
        }

        String userEmail = oAuth2Response.getEmail();
        User existData = userRepository.findByEmail(userEmail).orElse(null);

        if (existData == null) {
            // email 을 가진 유저가 없는 경우 (처음 가입)
            User newUser = User.builder()
                               .name(oAuth2Response.getName())
                               .email(oAuth2Response.getEmail())
                               .status(Status.ACTIVE)
                               .roleType(RoleType.USER).build();

            existData = userRepository.save(newUser);

        }

        SocialUserDTO socialUserDTO = new SocialUserDTO();
        socialUserDTO.setName(existData.getName());
        socialUserDTO.setEmail(existData.getEmail());
        socialUserDTO.setRole(existData.getRoleType());

        return new CustomOAuth2User(socialUserDTO);
    }
}
