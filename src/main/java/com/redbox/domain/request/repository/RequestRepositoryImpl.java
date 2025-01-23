package com.redbox.domain.request.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redbox.domain.request.dto.Filter;
import com.redbox.domain.request.dto.RequestFilter;
import com.redbox.domain.request.entity.QRequest;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.entity.RequestStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.redbox.domain.request.entity.QLike.like;

@RequiredArgsConstructor
public class RequestRepositoryImpl implements RequestRepositoryCustom {

    private static final Map<Filter, OrderSpecifier<?>> SORT_MAP = Map.of(
            Filter.END, QRequest.request.donationEndDate.asc(),
            Filter.HOT, QRequest.request.requestLikes.desc(),
            // 최신순 정렬은 createdAt 말고 id 순으로 정렬하는게 쥐똥만큼이라도 성능이점이 있을것 같기도...
            Filter.NEW, QRequest.request.createdAt.desc()
    );

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Request> searchBoards(Long userId, RequestFilter requestFilter, Pageable pageable) {
        QRequest request = QRequest.request;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(request.requestStatus.eq(RequestStatus.APPROVE));

        if (requestFilter.getStartDate() != null) {
            builder.and(request.donationStartDate.goe(requestFilter.getStartDate()));
        }

        if (requestFilter.getEndDate() != null) {
            builder.and(request.donationEndDate.loe(requestFilter.getEndDate()));
        }

        // 좋아요한 글만 필터링 (옵션이 LIKED일 경우)
        if (requestFilter.getOption() != null && requestFilter.getOption().equals(Filter.LIKED) && userId != null) {
            builder.and(
                    JPAExpressions.select(like.requestId)
                                  .from(like)
                                  .where(like.userId.eq(userId)
                                                    .and(like.isLiked.eq(true)))
                                  .exists()
            );
        }
        // 쿼리 실행
        JPAQuery<Request> query = queryFactory
                .selectFrom(request)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (requestFilter.getSort() != null) {
            query.orderBy(SORT_MAP.getOrDefault(requestFilter.getSort(), QRequest.request.createdAt.desc()));
        }

        List<Request> results = query.fetch();

        long total = queryFactory
                .selectFrom(request)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
