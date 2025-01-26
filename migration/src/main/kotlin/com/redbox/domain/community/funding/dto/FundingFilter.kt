package com.redbox.domain.community.funding.dto

import java.time.LocalDate

class FundingFilter {
    val page = 0
    val size = 0
    val sort: Filter? = null
    val option: Filter? = null
    val startDate: LocalDate? = null
    val endDate: LocalDate? = null
}
