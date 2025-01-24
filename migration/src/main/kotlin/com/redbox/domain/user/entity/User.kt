package com.redbox.domain.user.entity

import com.redbox.domain.user.exception.EmptyPasswordException
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import org.springframework.util.StringUtils
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    val id: Long? = null,

    val email: String,
    password: String,
    name: String,
    val birth: LocalDate,
    phoneNumber: String,
    roadAddress: String,
    extraAddress: String,
    detailAddress: String,

    @Enumerated(EnumType.STRING)
    val gender: Gender,

    @Enumerated(EnumType.STRING)
    val roleType: RoleType,

    status: Status,
) : BaseEntity() {

    var password: String = password
        protected set

    var name: String = name
        protected set

    var phoneNumber: String = phoneNumber
        protected set

    var roadAddress: String = roadAddress
        protected set

    var extraAddress: String = extraAddress
        protected set

    var detailAddress: String = detailAddress
        protected set

    @Enumerated(EnumType.STRING)
    var status: Status = status
        protected set

    var lastLoginAt: LocalDateTime? = null
        protected set

    fun changePassword(newPassword: String) {
        if (!StringUtils.hasText(newPassword)) {
            throw EmptyPasswordException()
        }
        this.password = newPassword
    }

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