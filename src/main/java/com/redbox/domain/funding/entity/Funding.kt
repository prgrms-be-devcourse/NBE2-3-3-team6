package com.redbox.domain.funding.entity

import com.redbox.domain.attach.entity.AttachFile
import com.redbox.global.entity.BaseEntity
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "fundings")
class Funding (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "funding_id")
    var fundingId: Long? = null,

    var userId: Long? = null,
    var userName: String? = null,
    var fundingTitle: String? = null,
    var fundingContent: String? = null,
    var targetAmount: Int = 0,
    var currentAmount: Int = 0,

    @Enumerated(EnumType.STRING)
    var fundingStatus: FundingStatus,
    @Enumerated(EnumType.STRING)
    var progress: FundingStatus,

    var fundingDate: LocalDate? = null,
    var donationStartDate: LocalDate? = null,
    var donationEndDate: LocalDate? = null,

    var fundingAttachFile: String? = null,

    @Enumerated(EnumType.STRING)
    var priority: Priority? = null,

    var fundingHits: Int = 0,
    var fundingLikes: Int = 0,

    @OneToMany(mappedBy = "funding", cascade = [CascadeType.ALL], orphanRemoval = true)
    var attachFiles: MutableList<AttachFile?>? = mutableListOf()

    ) : BaseEntity() {
    fun addAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()
        if (attachFile.isDuplicateIn(this.attachFiles)) return

        attachFiles!!.add(attachFile)
        attachFile.funding = this
    }

    fun removeAttachFiles(attachFile: AttachFile) {
        attachFile.validateNull()

        attachFiles!!.remove(attachFile)
        attachFile.funding = null
    }

    fun updateFunding(
        title: String?,
        content: String?,
        DonationStartDate: LocalDate?,
        DonationEndDate: LocalDate?,
        targetAmount: Int
    ) {
        this.fundingTitle = title
        this.fundingContent = content
        this.donationStartDate = DonationStartDate
        this.donationEndDate = DonationEndDate
        this.targetAmount = targetAmount
    }

    fun approve() {
        this.fundingStatus = FundingStatus.APPROVE
    }

    fun reject() {
        this.fundingStatus = FundingStatus.REJECT
    }

    fun drop() {
        this.fundingStatus = FundingStatus.DROP
    }

    fun expired() {
        this.progress = FundingStatus.EXPIRED
    }

    fun inProgress() {
        this.progress = FundingStatus.IN_PROGRESS
    }

    fun rejectProgress() {
        this.progress = FundingStatus.REJECT
    }

    fun dropProgress() {
        this.progress = FundingStatus.DROP
    }

    fun incrementHits() {
        this.fundingHits++
    }

    fun incrementLikes() {
        this.fundingLikes++
    }

    fun decrementLikes() {
        this.fundingLikes--
    }
}