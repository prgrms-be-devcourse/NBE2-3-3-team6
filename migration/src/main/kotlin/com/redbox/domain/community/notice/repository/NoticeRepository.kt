package com.redbox.domain.community.notice.repository

import com.redbox.domain.community.notice.entity.Notice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NoticeRepository : JpaRepository<Notice, Long?> {
    @Modifying
    @Query("UPDATE Notice n SET n.noticeHits = n.noticeHits + 1 WHERE n.id = :id")
    fun increaseHit(@Param("id") id: Long?)

    @Query(
        ("select n from Notice n" +
                " left join fetch n.attachFiles af" +
                " left join fetch n.user u" +
                " where n.id = :noticeId")
    )
    fun findForDetail(@Param("noticeId") id: Long?): Optional<Notice>

    @Query(
        ("select n from Notice n" +
                " left join fetch n.user u" +
                " where n.id = :noticeId")
    )
    fun findForUpdate(@Param("noticeId") id: Long?): Optional<Notice>

    @Query(
        ("select n from Notice n" +
                " left join fetch n.attachFiles af" +
                " where n.id = :noticeId")
    )
    fun findForDelete(@Param("noticeId") id: Long?): Optional<Notice>

    fun findTop5ByOrderByCreatedAtDesc(): List<Notice>

    @Modifying
    @Query("UPDATE Notice n SET n.noticeHits = n.noticeHits + :hits WHERE n.id = :noticeId")
    fun bulkUpdateHit(
        @Param("noticeId") noticeId: Long?,
        @Param("hits") hits: Long?
    )
}
