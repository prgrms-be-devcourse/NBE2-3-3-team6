package com.redbox.domain.community.attach.repository

import com.redbox.domain.community.attach.entity.AttachFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AttachFileRepository : JpaRepository<AttachFile, Long>