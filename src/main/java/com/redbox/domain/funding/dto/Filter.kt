package com.redbox.domain.funding.dto

enum class Filter(val filter: String? = null) {
    LIKED("관심글"), NEW("최신순"), END("만료순"), HOT("인기순");
}
