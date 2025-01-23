package com.redbox.domain.redcard.entity

import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import lombok.AccessLevel
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import java.time.LocalDate

@Getter
@Entity
@Table(name = "redcards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class Redcard @Builder constructor(//레드박스 소유시 0
    private var userId: Long,
    private var donationDate: LocalDate,
    private var serialNumber: String,
    private var hospitalName: String,
    @field:Enumerated(
        EnumType.STRING
    ) private var redcardStatus: RedcardStatus,
    @field:Enumerated(EnumType.STRING) private var ownerType: OwnerType
) :
    BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "redcard_id")
    private var id: Long? = null

    // 헌혈증 소지자가 바뀔때 사용하는 메서드
    fun updateUser(userId: Long) {
        this.userId = userId
    }

    fun changeRedcardStatus(status: RedcardStatus) {
        this.redcardStatus = status
    }

    fun changeOwnerType(ownerType: OwnerType) {
        this.ownerType = ownerType
    }
}
