package com.redbox.global.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity : BaseTimeEntity() {

    @CreatedBy
    @Column(updatable = false) // 한번 저장되면 수정 불가
    var createdBy: String? = null // 생성자
        private set

    @LastModifiedBy
    var updatedBy: String? = null // 수정자
        private set

    // 처음 회원가입 시 가입한 이메일을 세팅해주기 위해 생성
    fun setCreatedBy(createdBy: String?) {
        this.createdBy = createdBy
    }

    // 처음 회원가입 시 가입한 이메일을 세팅해주기 위해 생성
    fun setUpdatedBy(updatedBy: String?) {
        this.updatedBy = updatedBy
    }
}