package com.redbox.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

@Configuration
class ThymeleafConfig {

    @Bean
    fun templateEngine(templateResolver: ClassLoaderTemplateResolver): SpringTemplateEngine {
        return SpringTemplateEngine().apply {
            setTemplateResolver(templateResolver)
        }
    }

    @Bean
    fun templateResolver(): ClassLoaderTemplateResolver {
        return ClassLoaderTemplateResolver().apply {
            prefix = "templates/"
            suffix = ".html"
            templateMode = TemplateMode.HTML
            characterEncoding = "UTF-8"
        }
    }
}