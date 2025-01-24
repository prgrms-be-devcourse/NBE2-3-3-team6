package com.redbox.domain.redcard.entity

import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "redcards")
class Redcard(
    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(nullable = false)
    var donationDate: LocalDate,

    @Column(nullable = false, unique = true)
    var serialNumber: String,

    @Column(nullable = false)
    var hospitalName: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var redcardStatus: RedcardStatus,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var ownerType: OwnerType
) : BaseEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redcard_id")
    var id: Long? = null
        private set

    // 헌혈증 소지자가 바뀔 때 사용하는 메서드
    fun updateUser(userId: Long) {
        this.userId = userId
    }

    // 헌혈증 상태를 변경하는 메서드
    fun changeRedcardStatus(status: RedcardStatus) {
        this.redcardStatus = status
    }

    // 헌혈증 소유자 타입 변경
    fun changeOwnerType(ownerType: OwnerType) {
        this.ownerType = ownerType
    }

    override fun toString(): String {
        return "Redcard(userId=$userId, donationDate=$donationDate, serialNumber='$serialNumber', hospitalName='$hospitalName', redcardStatus=$redcardStatus, ownerType=$ownerType)"
    }

}
