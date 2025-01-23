package com.redbox.domain.funding.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.redbox.domain.funding.dto.Filter;
import com.redbox.domain.funding.dto.FundingFilter;
import com.redbox.domain.funding.entity.QFunding;
import com.redbox.domain.funding.entity.Funding;
import com.redbox.domain.funding.entity.FundingStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.redbox.domain.funding.entity.QLike.like;

@RequiredArgsConstructor
public class FundingRepositoryImpl implements FundingRepositoryCustom {

    private static final Map<Filter, OrderSpecifier<?>> SORT_MAP = Map.of(
            Filter.END, QFunding.funding.donationEndDate.asc(),
            Filter.HOT, QFunding.funding.fundingLikes.desc(),
            // 최신순 정렬은 createdAt 말고 id 순으로 정렬하는게 쥐똥만큼이라도 성능이점이 있을것 같기도...
            Filter.NEW, QFunding.funding.createdAt.desc()
    );

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Funding> searchBoards(Long userId, FundingFilter fundingFilter, Pageable pageable) {
        QFunding funding = QFunding.funding;

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(funding.fundingStatus.eq(FundingStatus.APPROVE));

        if (fundingFilter.getStartDate() != null) {
            builder.and(funding.donationStartDate.goe(fundingFilter.getStartDate()));
        }

        if (fundingFilter.getEndDate() != null) {
            builder.and(funding.donationEndDate.loe(fundingFilter.getEndDate()));
        }

        // 좋아요한 글만 필터링 (옵션이 LIKED일 경우)
        if (fundingFilter.getOption() != null && fundingFilter.getOption().equals(Filter.LIKED) && userId != null) {
            builder.and(
                    JPAExpressions.select(like.fundingId)
                                  .from(like)
                                  .where(like.userId.eq(userId)
                                                    .and(like.isLiked.eq(true)))
                                  .exists()
            );
        }
        // 쿼리 실행
        JPAQuery<Funding> query = queryFactory
                .selectFrom(funding)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (fundingFilter.getSort() != null) {
            query.orderBy(SORT_MAP.getOrDefault(fundingFilter.getSort(), QFunding.funding.createdAt.desc()));
        }

        List<Funding> results = query.fetch();

        long total = queryFactory
                .selectFrom(funding)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
