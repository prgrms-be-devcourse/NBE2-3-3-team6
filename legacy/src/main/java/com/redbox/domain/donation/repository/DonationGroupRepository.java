package com.redbox.domain.donation.repository;

import com.redbox.domain.donation.dto.Top5DonorResponse;
import com.redbox.domain.donation.entity.DonationGroup;
import com.redbox.domain.donation.entity.DonationStatus;
import com.redbox.domain.donation.entity.DonationType;
import com.redbox.domain.user.dto.DonationResponse;
import com.redbox.domain.user.dto.ReceptionResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonationGroupRepository extends JpaRepository<DonationGroup, Long> {

    long countByDonorId(Long donorId);

    @Query("SELECT SUM(d.donationAmount) FROM DonationGroup d WHERE d.donorId = :donorId AND d.donationStatus = 'DONE'")
    Integer sumDonationAmountByDonorId(@Param("donorId") Long donorId);

    @Query("SELECT COUNT(DISTINCT d.receiverId) FROM DonationGroup d WHERE d.donorId = :donorId AND d.receiverId != :redboxId AND d.donationStatus = 'DONE'")
    Integer countDistinctReceiverIdByDonorIdAndReceiverIdNot(@Param("donorId") Long donorId, @Param("redboxId") Long redboxId);
  
    @Query("SELECT COUNT(DISTINCT d.receiverId) FROM DonationGroup d WHERE d.donorId = 0 AND d.receiverId != 0 AND d.donationStatus = 'DONE'")
    Integer getHelpedPatientsCount();

    @Query("SELECT new com.redbox.domain.user.dto.DonationResponse(d, " +
            "CASE WHEN d.receiverId = 0 THEN '레드박스' ELSE u.name END) " +
            "FROM DonationGroup d " +
            "LEFT JOIN User u ON d.receiverId = u.id " +
            "WHERE d.donorId = :donorId " +
            "AND d.donationStatus = 'DONE'")
    Page<DonationResponse> findAllWithReceiverNameByDonorId(Long donorId, Pageable pageable);

    @Query("SELECT new com.redbox.domain.user.dto.ReceptionResponse(d, " +
            "CASE WHEN d.donorId = 0 THEN '레드박스' ELSE u.name END) " +
            "FROM DonationGroup d " +
            "LEFT JOIN User u ON d.receiverId = u.id " +
            "WHERE d.receiverId = :receiverId " +
            "AND d.donationStatus = 'DONE'")
    Page<ReceptionResponse> findAllWithDonorNameByReceiverId(Long receiverId, Pageable pageable);

    @Query("SELECT MAX(d.donationDate) FROM DonationGroup d WHERE d.donorId = :userId")
    Optional<LocalDate> findLastDonationDateByDonorId(@Param("userId") Long userId);
  
    @Query("SELECT new com.redbox.domain.donation.dto.Top5DonorResponse(" +
            "RANK() OVER (ORDER BY SUM(dg.donationAmount) DESC), " +
            "dg.donorId, u.name, SUM(dg.donationAmount)) " +
            "FROM DonationGroup dg " +
            "LEFT JOIN User u ON dg.donorId = u.id " +
            "WHERE dg.donorId != 0 " +
            "AND MONTH(dg.donationDate) = MONTH(CURRENT_DATE) " +
            "AND YEAR(dg.donationDate) = YEAR(CURRENT_DATE) " +
            "GROUP BY dg.donorId, u.name " +
            "ORDER BY SUM(dg.donationAmount) DESC " +
            "LIMIT 5")
    List<Top5DonorResponse> findTop5DonorsOfTheMonth();

    @Query("SELECT d FROM DonationGroup  d WHERE d.donorId =:donorId AND d.receiverId =:receiverId AND d.donationStatus =:donationStatus")
    DonationGroup findByDonorIdAndReceiverIdAndDonationStatus(@Param("donorId") long donorId, @Param("receiverId") long receiverId, @Param("donationType") DonationStatus donationStatus);

    List<DonationGroup> findByReceiverIdAndDonationStatus(long receiverId, DonationStatus donationStatus);

    @Query("SELECT SUM(d.donationAmount) FROM DonationGroup d where d.donationType = 'TO_REDBOX' and d.donationStatus = 'DONE'")
    Integer sumDonationAmountInRedbox();
}
