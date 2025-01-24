package com.redbox.domain.redcard.repository

import com.redbox.domain.redcard.entity.Redcard
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RedcardRepository : JpaRepository<Redcard, Long> {
    fun findBySerialNumber(serialNumber: String): Optional<Redcard>
}