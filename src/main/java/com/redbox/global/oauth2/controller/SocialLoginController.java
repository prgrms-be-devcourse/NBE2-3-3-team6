//package com.redbox.global.oauth.controller;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.client.registration.ClientRegistration;
//import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import jakarta.servlet.http.HttpSession;
//import lombok.AllArgsConstructor;
//
//import java.util.Map;
//import java.util.UUID;
//
//@RestController
//@AllArgsConstructor
//public class SocialLoginController {
//
//    private final ClientRegistrationRepository clientRegistrationRepository;
//
//    @GetMapping("/social/login/{provider}")
//    public Map<String, String> getAuthorizationUrl(@PathVariable String provider) {
//        System.out.println("Provider: " + provider); // 로그 추가
//        // ClientRegistration 가져오기
//        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
//        if (clientRegistration == null) {
//            throw new IllegalArgumentException("Unknown provider: " + provider);
//        }
////
//        String state = UUID.randomUUID().toString();
//        // 인증 URL 생성
//        String authorizationUrl = clientRegistration.getProviderDetails().getAuthorizationUri();
//        String clientId = clientRegistration.getClientId();
//        String redirectUri = clientRegistration.getRedirectUri();
//
//        String authUrl = String.format("%s?response_type=code&client_id=%s&redirect_uri=%s&state=%s",
//                authorizationUrl, clientId, redirectUri, state);
//
//        System.out.println("Generated authorization URL: " + authUrl);
//
//        return Map.of("authUrl", authUrl, "state", state);
//    }
//
//    @GetMapping("/login/oauth2/code/{provider}")
//    public String handleOAuth2Callback(@PathVariable String provider, @RequestParam String code, @RequestParam String state, HttpSession session) {
//        // 세션에 저장된 state 가져오기
//        String savedState = (String) session.getAttribute("oauth2_state");
//        if (savedState == null || !savedState.equals(state)) {
//            throw new IllegalStateException("Invalid state parameter");
//        }
//
//        // state 검증 후 세션에서 제거
//        session.removeAttribute("oauth2_state");
//
//        // 이후 인증 코드를 사용하여 액세스 토큰 요청 처리
//        return "redirect:/";
//    }
//}
