package com.redbox.domain.community.notice.entity

import com.redbox.domain.community.attach.entity.AttachFile
import com.redbox.domain.user.user.entity.User
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "notices")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User? = null,

    noticeTitle: String,
    noticeContent: String,
    noticeHits: Int? = 0,

    @OneToMany(mappedBy = "notice", cascade = [CascadeType.ALL], orphanRemoval = true)
    var attachFiles: MutableList<AttachFile> = mutableListOf()

) : BaseEntity() {

    var noticeTitle: String? = noticeTitle
        protected set

    @Lob
    var noticeContent: String? = noticeContent
        protected set

    var noticeHits: Int? = noticeHits
        protected set

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

    //TODO : notice update 시, 주석 해제
    /*fun updateNotice(request: UpdateNoticeRequest) {
        this.noticeTitle = request.title
        this.noticeContent = request.content
    }*/
}