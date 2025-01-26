package com.redbox.domain.community.funding.entity

import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "fundings")
class Funding (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "funding_id")
    val fundingId: Long? = null,
    val userId: Long? = null,
    // val userName: String? = null,

    fundingTitle: String? = null,
    fundingContent: String? = null,
    targetAmount: Int? = 0,
    currentAmount: Int? = 0,

    fundingStatus: FundingStatus,
    progress: FundingStatus,

    fundingDate: LocalDate? = null,
    donationStartDate: LocalDate? = null,
    donationEndDate: LocalDate? = null,

    //fundingAttachFile: String? = null,

    priority: Priority? = null,

    fundingHits: Int = 0,
    fundingLikes: Int = 0,

    //파일 엔터티 필요
    //@OneToMany(mappedBy = "funding", cascade = [CascadeType.ALL], orphanRemoval = true)
    //var attachFiles: MutableList<AttachFile?> = mutableListOf()

) : BaseEntity() {
    /* 파일
    fun addAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()
        if (attachFile.isDuplicateIn(this.attachFiles)) return

        attachFiles.add(attachFile)
        attachFile.funding = this
    }

    fun removeAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()

        attachFiles.remove(attachFile)
        attachFile.funding = null
    } */

    var fundingTitle: String? = fundingTitle
        protected set

    var fundingContent: String? = fundingContent
        protected set

    var targetAmount: Int? = targetAmount
        protected set

    var currentAmount: Int? = currentAmount
        protected set

    @Enumerated(EnumType.STRING)
    var fundingStatus: FundingStatus = fundingStatus
        protected set

    @Enumerated(EnumType.STRING)
    var progress: FundingStatus = progress
        protected set

    var fundingDate: LocalDate? = fundingDate
        protected set

    var donationStartDate: LocalDate? = donationStartDate
        protected set

    var donationEndDate: LocalDate? = donationEndDate
        protected set

    //var fundingAttachFile: String? = fundingAttachFile
    //    protected set

    @Enumerated(EnumType.STRING)
    var priority: Priority? = priority
        protected set

    var fundingHits: Int = fundingHits
        protected set

    var fundingLikes: Int = fundingLikes
        protected set
}