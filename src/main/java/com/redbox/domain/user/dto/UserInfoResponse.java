package com.redbox.domain.user.dto;

import com.redbox.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String email;
    private String name;
    private String phoneNumber;
    private String roadAddress;
    private String extraAddress;
    private String detailAddress;

    public UserInfoResponse(User user) {
        this.email = user.getEmail();
        this.name = user.getName();
        this.phoneNumber = user.getPhoneNumber();
        this.roadAddress = user.getRoadAddress();
        this.extraAddress = user.getExtraAddress();
        this.detailAddress = user.getDetailAddress();
    }
}