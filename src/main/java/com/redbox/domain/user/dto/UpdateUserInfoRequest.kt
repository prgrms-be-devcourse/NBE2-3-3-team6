package com.redbox.domain.user.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserInfoRequest {

    private String name;
    private String phoneNumber;
    private String roadAddress;
    private String extraAddress;
    private String detailAddress;
}
