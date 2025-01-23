package com.redbox.domain.funding.entity;

import com.redbox.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "likes")
public class Like extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // 좋아요 ID

    private Long fundingId; // 게시글 ID
    private Long userId; // 사용자 ID

    @Column(nullable = false)
    private boolean isLiked;

    @Builder
    public Like(Long fundingId, Long userId, boolean isLiked) {
        this.fundingId = fundingId;
        this.userId = userId;
        this.isLiked = isLiked;
    }

    public void falseLike(){this.isLiked = false;}
    public void trueLike(){this.isLiked = true;}

}
