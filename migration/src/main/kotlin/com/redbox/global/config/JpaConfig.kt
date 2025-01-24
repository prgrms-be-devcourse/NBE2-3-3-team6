package com.redbox.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JpaConfig {

    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAwareImpl()
    }
}