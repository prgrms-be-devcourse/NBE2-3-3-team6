package com.redbox.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DropInfoRequest {

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

}
