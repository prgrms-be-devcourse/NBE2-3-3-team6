package com.redbox.domain.community.notice.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.redbox.domain.community.notice.dto.NoticeListResponse
import com.redbox.domain.community.notice.entity.QNotice
import com.redbox.domain.user.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class NoticeQueryRepository(
    private val queryFactory: JPAQueryFactory
) {
    private val notice: QNotice = QNotice.notice
    private val user: QUser = QUser.user

    fun findNotices(pageable: Pageable): Page<NoticeListResponse> {
        // 데이터 조회
        val response = queryFactory.select(
            Projections.constructor(
                NoticeListResponse::class.java,
                notice.id.`as`("noticeNo"),
                notice.noticeTitle.`as`("title"),
                notice.createdAt.`as`("createdDate"),
                user.name.coalesce("Unknown").`as`("writer"),
                notice.noticeHits.`as`("views"),
                notice.attachFiles.isNotEmpty().`as`("hasAttachFiles")
            )
        ).from(notice)
            .leftJoin(notice.user, user)
            .orderBy(notice.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        // 전체 카운트 조회
        val total = queryFactory.select(notice.count())
            .from(notice)
            .fetchOne() ?: 0L

        return PageImpl(response, pageable, total)
    }
}

