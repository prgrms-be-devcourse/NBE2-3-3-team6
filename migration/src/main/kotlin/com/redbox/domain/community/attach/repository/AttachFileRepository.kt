package com.redbox.domain.community.attach.repository

import com.redbox.domain.community.attach.entity.AttachFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface AttachFileRepository : JpaRepository<AttachFile, Long> {
    @Query("SELECT af FROM AttachFile af WHERE af.funding.fundingId = :fundingId")
    fun findAttachFiles(fundingId: Long): List<AttachFile>
}