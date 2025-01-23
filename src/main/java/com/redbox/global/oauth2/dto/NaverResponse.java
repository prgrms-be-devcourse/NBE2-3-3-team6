package com.redbox.global.oauth2.dto;

import java.util.Map;

public class NaverResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        // naver는 로그인 응답 안에 유저 정보가 response 이름으로 한번 더 감싸져서 오기 때문에 get 으로 한번 더 처리
        this.attribute = (Map<String, Object>) attribute.get("response");
    }
    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return attribute.get("email").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
