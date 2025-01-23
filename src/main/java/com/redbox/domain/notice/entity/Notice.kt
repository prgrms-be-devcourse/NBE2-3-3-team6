package com.redbox.domain.notice.entity

import com.redbox.domain.attach.entity.AttachFile
import com.redbox.domain.notice.dto.UpdateNoticeRequest
import com.redbox.domain.user.entity.User
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "notices")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: User,

    var noticeTitle: String,

    @Lob
    var noticeContent: String,

    var noticeHits: Int
) : BaseEntity() {

    @OneToMany(mappedBy = "notice", cascade = [CascadeType.ALL], orphanRemoval = true)
    var attachFiles: MutableList<AttachFile> = mutableListOf()

    // 연관관계 편의 메서드
    fun addAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()
        if (attachFile.isDuplicateIn(this.attachFiles)) return

        this.attachFiles.add(attachFile)
        attachFile.notice = this
    }

    fun removeAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()

        this.attachFiles.remove(attachFile)
        attachFile.notice = null
    }

    fun updateNotice(request: UpdateNoticeRequest) {
        this.noticeTitle = request.title
        this.noticeContent = request.content
    }
}