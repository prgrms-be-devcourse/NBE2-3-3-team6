package com.redbox.domain.attach.entity

import com.redbox.domain.attach.exception.NullAttachFileException
import com.redbox.domain.notice.entity.Notice
import com.redbox.domain.funding.entity.Funding
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "attach_files")
class AttachFile (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_id")
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    var category: Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_id")
    var funding: Funding? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    var notice: Notice? = null,

    var originalFilename: String,
    var newFilename: String
) : BaseEntity() {

    fun validateNull() {
        if (this == null) {
            throw NullAttachFileException()
        }
    }

    fun isDuplicateIn(attachFiles: List<AttachFile?>): Boolean {
        return attachFiles.contains(this)
    }

    fun belongToPost(postId: Long): Boolean {
        return when (this.category) {
            Category.NOTICE -> isNoticeFile(postId)
            Category.FUNDING -> isFundingFile(postId)

        }
    }

    private fun isNoticeFile(postId: Long): Boolean {
        // notice_id 값이 있다면, notice 필드를 프록시 객체로 설정하기 때문에
        return this.notice != null
    }

    private fun isFundingFile(postId: Long): Boolean {
        return this.funding != null
    }
}

