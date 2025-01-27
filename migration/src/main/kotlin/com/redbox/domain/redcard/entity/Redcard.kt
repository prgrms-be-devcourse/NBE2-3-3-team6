package com.redbox.domain.redcard.entity

import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "redcards")
class Redcard(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redcard_id")
    val id: Long? = null,

    @Column(nullable = false)
    val donationDate: LocalDate,

    @Column(nullable = false, unique = true)
    val serialNumber: String,

    @Column(nullable = false)
    val hospitalName: String,

    userId: Long,
    redcardStatus: RedcardStatus,
    ownerType: OwnerType

) : BaseEntity() {

    var userId: Long = userId
        protected set

    @Enumerated(EnumType.STRING)
    var redcardStatus: RedcardStatus = redcardStatus
        protected set

    @Enumerated(EnumType.STRING)
    var ownerType: OwnerType = ownerType
        protected set

    // 상태 변경 메서드
    fun updateUser(userId: Long) {
        this.userId = userId
    }

    fun changeRedcardStatus(status: RedcardStatus) {
        this.redcardStatus = status
    }

    fun changeOwnerType(ownerType: OwnerType) {
        this.ownerType = ownerType
    }

    // TODO: 테스트를 위해 추가된 toString() 메서드 (나중에 상의 후 제거)
    override fun toString(): String {
        return "Redcard(userId=$userId, donationDate=$donationDate, serialNumber='$serialNumber', hospitalName='$hospitalName', redcardStatus=$redcardStatus, ownerType=$ownerType)"
    }
}

