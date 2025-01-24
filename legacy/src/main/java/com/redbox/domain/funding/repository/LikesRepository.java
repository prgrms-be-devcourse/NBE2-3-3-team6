package com.redbox.domain.funding.repository;

import com.redbox.domain.funding.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Like, Long> {

    Like findByUserIdAndFundingId(Long userId, Long fundingId);
}
