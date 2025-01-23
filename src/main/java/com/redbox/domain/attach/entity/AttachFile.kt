package com.redbox.domain.attach.entity;

import com.redbox.domain.attach.exception.NullAttachFileException;
import com.redbox.domain.notice.entity.Notice;
import com.redbox.domain.request.entity.Request;
import com.redbox.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "attach_files")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttachFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attach_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_id")
    private Notice notice;

    private String originalFilename;
    private String newFilename;

    @Builder
    public AttachFile(Category category, Notice notice, Request request, String originalFilename, String newFilename) {
        this.category = category;
        this.notice = notice;
        this.request = request;
        this.originalFilename = originalFilename;
        this.newFilename = newFilename;
    }

    // 연관 관계 편의 메서드를 위한 setter 메서드
    @SuppressWarnings("lombok")
    public void setNotice(Notice notice) {
        this.notice = notice;
    }
    @SuppressWarnings("lombok")
    public void setRequest(Request request) {
        this.request = request;
    }

    public void validateNull() {
        if (this == null) {
            throw new NullAttachFileException();
        }
    }

    public boolean isDuplicateIn(List<AttachFile> attachFiles) {
        return attachFiles.contains(this);
    }

    public boolean belongToPost(Long postId) {
        return switch (this.category) {
            case NOTICE -> isNoticeFile(postId);
            case REQUEST -> isRequestFile(postId);
        };
    }

    private boolean isNoticeFile(Long postId) {
        // notice_id 값이 있다면, notice 필드를 프록시 객체로 설정하기 때문에
        return this.notice != null;
    }

    private boolean isRequestFile(Long postId) {
        return this.request != null;
    }
}
