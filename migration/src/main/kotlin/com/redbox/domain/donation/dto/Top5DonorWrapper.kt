package com.redbox.domain.donation.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonDeserialize
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
data class Top5DonorWrapper(
    val donors: List<Top5DonorResponse>
) {
    @JsonCreator
    constructor() : this(emptyList())
}