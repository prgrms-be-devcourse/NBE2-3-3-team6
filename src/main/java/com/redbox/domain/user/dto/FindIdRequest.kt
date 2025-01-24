package com.redbox.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FindIdRequest {
    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 형식에 맞게 입력해주세요. 010-0000-0000")
    @NotBlank(message = "연락처를 입력해주세요.")
    private String phoneNumber;
}