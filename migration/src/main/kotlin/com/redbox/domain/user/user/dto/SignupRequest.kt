package com.redbox.domain.user.user.dto

import com.redbox.domain.user.user.entity.Gender
import com.redbox.domain.user.user.entity.RoleType
import com.redbox.domain.user.user.entity.Status
import com.redbox.domain.user.user.entity.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

data class SignupRequest(
    @field:Email(message = "이메일 형식이 맞지 않습니다.")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
        message = "비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다."
    )
    val password: String,

    @field:NotBlank(message = "이름을 입력해주세요.")
    val userName: String,

    @field:NotNull(message = "성별을 선택해주세요.")
    val gender: Gender,

    val birth: LocalDate? = null,

    @field:Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 형식에 맞게 입력해주세요. 010-0000-0000")
    @field:NotBlank(message = "연락처를 입력해주세요.")
    val phoneNumber: String,

    val roadAddress: String? = null,
    val extraAddress: String? = null,
    val detailAddress: String? = null,

    val verified: Boolean
) {
    // Java 코드와의 호환성을 위한 메서드들
    fun isVerified() = verified

    companion object {
        @JvmStatic
        fun toEntity(request: SignupRequest, password: String): User = User(
            email = request.email,
            password = password,
            name = request.userName,
            gender = request.gender,
            birth = request.birth,
            phoneNumber = request.phoneNumber,
            roadAddress = request.roadAddress,
            extraAddress = request.extraAddress,
            detailAddress = request.detailAddress,
            roleType = RoleType.USER,
            status = Status.ACTIVE
        )
    }
}