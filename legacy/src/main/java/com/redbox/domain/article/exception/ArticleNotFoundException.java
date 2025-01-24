package com.redbox.domain.article.exception;

import com.redbox.global.exception.BusinessException;
import com.redbox.global.exception.ErrorCode;

public class ArticleNotFoundException extends BusinessException {
    public ArticleNotFoundException() {
        super(ErrorCode.FAIL_TO_FIND_ARTICLE);
    }
}
