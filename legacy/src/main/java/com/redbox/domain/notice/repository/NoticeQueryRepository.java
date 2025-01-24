package com.redbox.domain.notice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redbox.domain.notice.dto.NoticeListResponse;
import com.redbox.domain.notice.entity.QNotice;
import com.redbox.domain.user.entity.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NoticeQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final QNotice notice = QNotice.notice;
    private final QUser user = QUser.user;

    public Page<NoticeListResponse> findNotices(Pageable pageable) {
        // 데이터 조회
        List<NoticeListResponse> response =
                queryFactory.select(Projections.constructor(NoticeListResponse.class,
                        notice.id.as("noticeNo"),
                        notice.noticeTitle.as("title"),
                        notice.createdAt.as("createdDate"),
                        user.name.coalesce("Unknown").as("writer"),
                        notice.noticeHits.as("views"),
                        notice.attachFiles.isNotEmpty().as("hasAttachFiles")
                        )).from(notice)
                        .leftJoin(notice.user, user)
                        .orderBy(notice.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        // 전체 카운트 조회
        long total = Optional.ofNullable(
                queryFactory.select(notice.count())
                .from(notice)
                .fetchOne()
            ).orElse(0L);

        return new PageImpl<>(response, pageable, total);
    }
}
