package com.redbox.domain.community.attach.entity

enum class Category(val text: String? = null, val path: String? = null) {
    FUNDING("요청게시판", "funding"), NOTICE("공지사항", "notice")
}