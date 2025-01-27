package com.redbox.domain.donation.entity

class Redcard(
    val id: Long,
    var userId: Long
) {
    // 임시로 만든 엔티티로 추후 RedCard 도메인과 합칠경우 삭제 예정
    fun getCardId(): Long {
        return id
    }

    fun changeUserId(userId: Long) {
        this.userId = userId
    }
}