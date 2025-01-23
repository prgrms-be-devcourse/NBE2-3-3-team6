package com.redbox.domain.request.repository;

import com.redbox.domain.request.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Like, Long> {

    Like findByUserIdAndRequestId(Long userId, Long requestId);
}
