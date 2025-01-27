package com.redbox.domain.community.funding.repository

import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import com.redbox.domain.community.funding.dto.Filter
import com.redbox.domain.community.funding.dto.FundingFilter
import com.redbox.domain.community.funding.entity.Funding
import com.redbox.domain.community.funding.entity.FundingStatus
import com.redbox.domain.community.funding.entity.QFunding
import com.redbox.domain.funding.repository.FundingRepositoryCustom
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class FundingRepositoryImpl(
    val queryFactory: JPAQueryFactory
) : FundingRepositoryCustom {

    companion object {
        private val SORT_MAP: Map<Filter?, OrderSpecifier<*>> = java.util.Map.of<Filter?, OrderSpecifier<*>>(
            Filter.END,
            QFunding.funding.donationEndDate.asc(),
            Filter.HOT,
            QFunding.funding.fundingLikes.desc(),  // 최신순 정렬은 createdAt 말고 id 순으로 정렬하는게 쥐똥만큼이라도 성능이점이 있을것 같기도...
            Filter.NEW,
            QFunding.funding.createdAt.desc()
        )
    }

    override fun searchBoards(userId: Long?, fundingFilter: FundingFilter, pageable: Pageable): Page<Funding> {
        val funding = QFunding.funding
        val builder = BooleanBuilder()

        builder.and(funding.fundingStatus.eq(FundingStatus.APPROVE))

        fundingFilter.startDate?.let {
            builder.and(funding.donationStartDate.goe(it))
        }

        fundingFilter.endDate?.let {
            builder.and(funding.donationEndDate.loe(it))
        }

        // 좋아요한 글만 필터링 (옵션이 LIKED일 경우)
        /*if (fundingFilter.option == Filter.LIKED && userId != null) {
            builder.and(
                JPAExpressions.select(like.fundingId)
                    .from(like)
                    .where(
                        like.userId.eq(userId)
                            .and(like.isLiked.eq(true))
                    )
                    .exists()
            )
        }*/

        // 쿼리 실행
        val query = queryFactory
            .selectFrom(funding)
            .where(builder)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())

        fundingFilter.sort?.let {
            query.orderBy(SORT_MAP[it] ?: QFunding.funding.createdAt.desc())
        }

        val results = query.fetch()

        val total = queryFactory
            .selectFrom(funding)
            .where(builder)
            .fetchCount()

        return PageImpl(results, pageable, total)
    }
}
