package com.redbox.domain.redcard.repository

import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RedcardRepository : JpaRepository<Redcard, Long> {

    fun findBySerialNumber(serialNumber: String): Redcard?

    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Redcard>

    fun findByUserIdAndRedcardStatus(userId: Long, redcardStatus: RedcardStatus, pageable: Pageable): List<Redcard>
  
    fun findByUserIdAndId(userId: Long, id: Long): Redcard?

    fun existsBySerialNumber(serialNumber: String): Boolean
}