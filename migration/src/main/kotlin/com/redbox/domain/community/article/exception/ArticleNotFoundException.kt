package com.redbox.domain.community.article.exception

import com.redbox.global.exception.BusinessException
import com.redbox.global.exception.ErrorCode

class ArticleNotFoundException: BusinessException(ErrorCode.FAIL_TO_FIND_ARTICLE) {
}