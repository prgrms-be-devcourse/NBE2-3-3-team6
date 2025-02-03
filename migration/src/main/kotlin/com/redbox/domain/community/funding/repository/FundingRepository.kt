package com.redbox.domain.funding.repository

import com.redbox.domain.community.funding.dto.AdminDetailProjection
import com.redbox.domain.community.funding.dto.AdminListResponse
import com.redbox.domain.community.funding.dto.FundingListResponse
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface FundingRepository : JpaRepository<Funding, Long>, FundingRepositoryCustom {

    @Query("""
        SELECT 
            f.fundingId as fundingId,
            f.fundingTitle as fundingTitle,
            u.name as writer,
            CAST(f.createdAt as LocalDate) as createdDate,
            CASE f.fundingStatus
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.REQUEST THEN '요청'
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.APPROVE THEN '승인'
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.REJECT THEN '거절'
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.EXPIRED THEN '만료'
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.IN_PROGRESS THEN '진행중'
                WHEN com.redbox.domain.community.funding.entity.FundingStatus.DROP THEN '삭제'
            END as status,
            f.fundingHits as hits,
            f.fundingLikes as likes
        FROM Funding f
        LEFT JOIN User u ON f.userId = u.id
        WHERE f.userId = :id
    """)
    fun findMyFundings(id: Long, pageable: Pageable): Page<FundingListResponse>

  
    @Query("""
    SELECT f.fundingId as id, 
           f.fundingTitle as title, 
           u.name as author,
           f.createdAt as date, 
           '요청' as status 
    FROM Funding f
    LEFT JOIN User u ON f.userId = u.id
    WHERE f.fundingStatus = 'REQUEST'
    """)
    fun findAllByStatusRequest(): List<AdminListResponse>

  
    @Query("""
        SELECT
            u.name as writer
        FROM Funding f
        LEFT JOIN User u ON f.userId = u.id
        WHERE f.fundingId = :fundingId
    """)
    fun findUserNameByFundingId(fundingId: Long): String?

    @Query("""
    SELECT f.fundingId as id,
           f.fundingTitle as title,
           u.name as author,
           f.createdAt as date,
           f.donationStartDate as startDate,
           f.donationEndDate as endDate,
           f.targetAmount as targetAmount,
           CASE f.fundingStatus
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.REQUEST THEN '요청'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.APPROVE THEN '승인'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.REJECT THEN '거절'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.EXPIRED THEN '만료'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.IN_PROGRESS THEN '진행중'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.DROP THEN '삭제'
           END as status,
           f.fundingHits as views,
           f.fundingContent as content
    FROM Funding f
    LEFT JOIN User u ON f.userId = u.id
    WHERE f.fundingId = :fundingId
    """)
    fun findDetailById(fundingId: Long): AdminDetailProjection?

    @Query("""
    SELECT f.fundingId as id, 
           f.fundingTitle as title, 
           u.name as author,
           f.createdAt as date, 
           '승인' as status 
    FROM Funding f
    LEFT JOIN User u ON f.userId = u.id
    WHERE f.fundingStatus = 'APPROVE' 
    AND f.progress = 'IN_PROGRESS' 
    order by f.fundingLikes desc limit 5
    """)
    fun findTop5FundingWithLikeCount(): List<AdminListResponse>

    @Query("""
    SELECT f.fundingId as id, 
           f.fundingTitle as title, 
           u.name as author,
           f.createdAt as date, 
           CASE f.fundingStatus
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.REQUEST THEN '요청'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.APPROVE THEN '승인'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.REJECT THEN '거절'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.EXPIRED THEN '만료'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.IN_PROGRESS THEN '진행중'
               WHEN com.redbox.domain.community.funding.entity.FundingStatus.DROP THEN '삭제'
           END as status
    FROM Funding f
    LEFT JOIN User u ON f.userId = u.id
    join fetch Like l on f.fundingId = l.fundingId
    WHERE l.userId = :userId 
    and l.isLiked = true 
    order by l.updatedAt desc limit 5
    """)
    fun findLikedTop5FundingsByUserId(userId: Long): List<AdminListResponse>

    @Query("SELECT COUNT(f) FROM Funding f WHERE f.fundingStatus = :fundingStatus")
    fun countByFundingStatus(fundingStatus: FundingStatus): Int?

    @Query("SELECT COUNT(r) FROM Funding r WHERE r.userId = :userId AND r.fundingStatus = 'APPROVE' AND r.progress = 'IN_PROGRESS'")
    fun countInProgressFundingsByUserId(@Param("userId") userId: Long): Int

}