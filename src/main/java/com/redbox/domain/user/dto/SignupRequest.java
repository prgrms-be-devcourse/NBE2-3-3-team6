package com.redbox.domain.user.dto;

import com.redbox.domain.user.entity.Gender;
import com.redbox.domain.user.entity.RoleType;
import com.redbox.domain.user.entity.Status;
import com.redbox.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignupRequest {

    @Email(message = "이메일 형식이 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String userName;

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;

    private LocalDate birth;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 형식에 맞게 입력해주세요. 010-0000-0000")
    @NotBlank(message = "연락처를 입력해주세요.")
    private String phoneNumber;

    private String roadAddress;
    private String extraAddress;
    private String detailAddress;

    private boolean verified;

    public static User toEntity(SignupRequest request, String password) {
        return User.builder()
                .email(request.getEmail())
                .password(password)
                .name(request.getUserName())
                .gender(request.getGender())
                .birth(request.getBirth())
                .phoneNumber(request.getPhoneNumber())
                .roadAddress(request.getRoadAddress())
                .extraAddress(request.getExtraAddress())
                .detailAddress(request.getDetailAddress())
                .roleType(RoleType.USER)
                .status(Status.ACTIVE)
                .build();
    }
}
