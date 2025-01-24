package com.redbox.global.config

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

class AuditorAwareImpl : AuditorAware<String> {

    override fun getCurrentAuditor(): Optional<String> {
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        return if (authentication == null || !authentication.isAuthenticated || authentication.principal == "anonymousUser") {
            Optional.empty()
        } else {
            Optional.of(authentication.name)
        }
    }
}