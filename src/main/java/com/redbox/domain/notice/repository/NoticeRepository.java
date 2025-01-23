package com.redbox.domain.notice.repository;

import com.redbox.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Modifying
    @Query("UPDATE Notice n SET n.noticeHits = n.noticeHits + 1 WHERE n.id = :id")
    void increaseHit(@Param("id") Long id);

    @Query("select n from Notice n" +
            " left join fetch n.attachFiles af" +
            " left join fetch n.user u" +
            " where n.id = :noticeId")
    Optional<Notice> findForDetail(@Param("noticeId") Long id);

    @Query("select n from Notice n" +
            " left join fetch n.user u" +
            " where n.id = :noticeId")
    Optional<Notice> findForUpdate(@Param("noticeId") Long id);

    @Query("select n from Notice n" +
            " left join fetch n.attachFiles af" +
            " where n.id = :noticeId")
    Optional<Notice> findForDelete(@Param("noticeId") Long id);

    List<Notice> findTop5ByOrderByCreatedAtDesc();

    @Modifying
    @Query("UPDATE Notice n SET n.noticeHits = n.noticeHits + :hits WHERE n.id = :noticeId")
    void bulkUpdateHit(@Param("noticeId") Long noticeId, @Param("hits") Long hits);

}
