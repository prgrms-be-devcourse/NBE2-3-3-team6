package com.redbox.global.oauth2.dto;

public interface OAuth2Response {

    // 제공자 (EX. naver, google, ...)
    String getProvider();

    // 제공자에서 발급해주는 아이디(번호)
    String getProviderId();

    // email
    String getEmail();

    // 사용자 이름 (제공자에서 설정한 이름)
    String getName();
}
