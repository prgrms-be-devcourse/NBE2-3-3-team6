package com.redbox.domain.community.funding.entity

import com.redbox.domain.community.attach.entity.AttachFile
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

    fundingTitle: String? = null,
    fundingContent: String? = null,
    targetAmount: Int? = 0,
    currentAmount: Int? = 0,

    fundingStatus: FundingStatus,
    progress: FundingStatus,

    donationStartDate: LocalDate? = null,
    donationEndDate: LocalDate? = null,

    priority: Priority? = null,

    fundingHits: Int = 0,
    fundingLikes: Int = 0,

    @OneToMany(mappedBy = "funding", cascade = [CascadeType.ALL], orphanRemoval = true)
    var attachFiles: MutableList<AttachFile?> = mutableListOf()

) : BaseEntity() {

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

    var donationStartDate: LocalDate? = donationStartDate
        protected set

    var donationEndDate: LocalDate? = donationEndDate
        protected set

    @Enumerated(EnumType.STRING)
    var priority: Priority? = priority
        protected set

    var fundingHits: Int = fundingHits
        protected set

    var fundingLikes: Int = fundingLikes
        protected set

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

    // 상태 변경 메서드
    fun approve() { this.fundingStatus = FundingStatus.APPROVE }
    fun reject() { this.fundingStatus = FundingStatus.REJECT }
    fun drop() { this.fundingStatus = FundingStatus.DROP }
    fun expired() { this.progress = FundingStatus.EXPIRED }
    fun inProgress() { this.progress = FundingStatus.IN_PROGRESS }
    fun rejectProgress() { this.progress = FundingStatus.REJECT }
    fun dropProgress() { this.progress = FundingStatus.DROP }
    fun incrementHits() { this.fundingHits++ }
    fun incrementLikes() { this.fundingLikes++ }
    fun decrementLikes() { this.fundingLikes-- }
}