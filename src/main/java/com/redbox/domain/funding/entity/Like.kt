package com.redbox.domain.funding.entity

import com.redbox.global.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "likes")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    var fundingId: Long,
    var userId: Long,

    @Column(nullable = false)
    var isLiked: Boolean

) : BaseTimeEntity() {

    fun falseLike() {
        this.isLiked = false
    }
    fun trueLike() {
        this.isLiked = true
    }
}