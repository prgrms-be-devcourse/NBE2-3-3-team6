package com.redbox.domain.community.funding.entity

import com.redbox.global.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "likes")
class Like(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    val fundingId: Long? = null,
    val userId: Long? = null,

    isLiked: Boolean,

) : BaseTimeEntity() {

    var isLiked: Boolean = isLiked
        protected set

    fun falseLike() {
        this.isLiked = false
    }
    fun trueLike() {
        this.isLiked = true
    }
}