package com.redbox.global.config

import com.redbox.global.auth.filter.JWTFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@Profile("!test")
class SecurityConfig(
    private val jwtFilter: JWTFilter, // ✅ JWT 필터 주입
    private val authenticationConfiguration: AuthenticationConfiguration
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/auth/login", "/auth/signup", "/auth/logout", "/auth/email/**").permitAll()
                    .anyRequest().authenticated() // ✅ 나머지는 인증 필요
            }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .sessionManagement { it.disable() } // ✅ Stateless 방식

        return http.build()
    }
}
