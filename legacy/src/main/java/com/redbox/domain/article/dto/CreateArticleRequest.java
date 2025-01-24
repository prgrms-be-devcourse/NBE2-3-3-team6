package com.redbox.domain.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateArticleRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    private String subject;

    @NotBlank(message = "기사 URL을 입력해주세요.")
    private String url;

    @NotBlank(message = "출처를 입력해주세요.")
    private String source;
}
