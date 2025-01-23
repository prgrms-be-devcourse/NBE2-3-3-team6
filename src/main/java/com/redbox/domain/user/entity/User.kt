package com.redbox.domain.user.entity

import com.redbox.domain.user.exception.EmptyPasswordException
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.util.StringUtils
import java.time.LocalDate

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    var id: Long? = null,

    var email: String,
    var password: String,
    var name: String,
    var birth: LocalDate? = null,
    var phoneNumber: String,

    var roadAddress: String? = null,
    var extraAddress: String? = null,
    var detailAddress: String? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender,

    @Enumerated(EnumType.STRING)
    var roleType: RoleType,

    @Enumerated(EnumType.STRING)
    var status: Status,
) : BaseEntity() {

    fun changePassword(newPassword: String) {
        if (!StringUtils.hasText(newPassword)) {
            throw EmptyPasswordException()
        }
        this.password = newPassword
    }

    // 회원 탈퇴 상태 변경
    fun inactive() {
        this.status = Status.INACTIVE
    }

    fun changeName(name: String) {
        this.name = name
    }

    fun changePhoneNumber(phoneNumber: String) {
        this.phoneNumber = phoneNumber
    }

    fun changeRoadAddress(roadAddress: String) {
        this.roadAddress = roadAddress
    }

    fun changeExtraAddress(extraAddress: String) {
        this.extraAddress = extraAddress
    }

    fun changeDetailAddress(detailAddress: String) {
        this.detailAddress = detailAddress
    }
}
