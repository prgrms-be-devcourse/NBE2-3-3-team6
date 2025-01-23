package com.redbox.domain.redcard.repository;

import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RedcardRepository extends JpaRepository<Redcard, Long> {

    Optional<Redcard> findBySerialNumber(String serialNumber);

    Page<Redcard> findAllByUserId(Long userId, Pageable pageable);

    List<Redcard> findByUserId(Long userId);

    Optional<Redcard> findByUserIdAndId(Long userId, Long id);

    List<Redcard> findByUserIdAndRedcardStatus(Long userId, RedcardStatus redcardStatus);

    @Query("SELECT COUNT(r) FROM Redcard r WHERE r.ownerType = 'REDBOX' AND r.userId = 0")
    Integer countAllInRedbox();
}

