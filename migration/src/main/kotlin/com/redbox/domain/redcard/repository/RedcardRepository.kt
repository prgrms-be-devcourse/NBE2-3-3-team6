package com.redbox.domain.redcard.repository

import com.redbox.domain.redcard.entity.Redcard
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface RedcardRepository : JpaRepository<Redcard, Long> {

    fun findBySerialNumber(serialNumber: String): Redcard?

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Redcard>

    fun findByUserIdAndId(userId: Long, id: Long): Redcard?
}
