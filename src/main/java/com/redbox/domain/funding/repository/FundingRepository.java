package com.redbox.domain.funding.repository;

import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.entity.FundingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FundingRepository extends JpaRepository<Funding, Long>, FundingRepositoryCustom {
    List<Funding> findByFundingStatus(FundingStatus fundingStatus);

    List<Funding> findByDonationEndDateBeforeAndProgressNot(LocalDate date, FundingStatus progress);

    boolean existsByFundingIdAndUserId(Long fundingId, Long userId);

    @Query("SELECT r FROM Funding r WHERE r.userId = :userId AND r.fundingStatus != 'DROP'")
    Page<Funding> findAllByUserIdAndNotDropStatus(Long userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Funding r WHERE r.userId = :userId AND r.fundingStatus = 'APPROVE' AND r.progress = 'IN_PROGRESS'")
    int countInProgressFundingsByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM Funding r WHERE r.fundingStatus = 'APPROVE' AND r.progress = 'IN_PROGRESS'")
    int countAllInProgressFundings();

    @Query("SELECT r FROM Funding r WHERE r.fundingStatus = 'APPROVE' AND r.progress = 'IN_PROGRESS' order by r.fundingLikes desc limit 5")
    List<Funding> findTop5FundingWithLikeCount();

    @Query("SELECT r FROM Funding r join fetch Like l on r.fundingId = l.fundingId WHERE l.userId = :userId and l.isLiked = true order by l.updatedAt desc limit 5")
    List<Funding> findLikedTop5FundingsByUserId(Long userId);

    @Query("SELECT COUNT(r) FROM Funding r WHERE r.fundingStatus = :fundingStatus")
    Integer countByFundingStatus(FundingStatus fundingStatus);
}