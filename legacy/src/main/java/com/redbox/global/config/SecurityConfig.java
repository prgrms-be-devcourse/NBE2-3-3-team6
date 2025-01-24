package com.redbox.global.config;


import com.redbox.global.oauth2.repository.CustomClientRegistrationRepo;
import com.redbox.global.oauth2.service.CustomOAuth2UserService;

import com.redbox.domain.auth.filter.CustomLogoutFilter;
import com.redbox.domain.auth.filter.JWTFilter;
import com.redbox.domain.auth.service.RefreshTokenService;
import com.redbox.domain.auth.util.JWTUtil;
import com.redbox.domain.auth.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;

import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService; // 변경: RefreshTokenService 주입
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomClientRegistrationRepo customClientRegistrationRepo;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomClientRegistrationRepo customClientRegistrationRepo, AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customClientRegistrationRepo = customClientRegistrationRepo;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService; // 주입
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
                .authorizeHttpRequests(auth -> auth
                        // ===== 공개 엔드포인트 =====
                        .requestMatchers("/auth/**").permitAll() // 인증 관련
                        .requestMatchers(HttpMethod.GET, "/articles", "/articles/{articleId}").permitAll() // 게시글 조회
                        .requestMatchers(HttpMethod.GET, "/redbox/stats").permitAll() // 통계 정보
                        .requestMatchers(HttpMethod.GET, "/requests/**").permitAll() // 요청 게시글 조회
                        .requestMatchers(HttpMethod.GET, "/notices", "/notices/{noticeId}", "/notices/top5").permitAll() // 공지사항 조회 및 최신 공지

                        // ===== 사용자 관련 엔드포인트 =====
                        .requestMatchers("/users/my-info/**").authenticated() // 사용자 정보 (보호됨)
                        .requestMatchers(HttpMethod.GET, "/users/my-donation-stats").authenticated() // 사용자 기부 통계

                        // ===== 요청 관련 엔드포인트 =====
                        .requestMatchers(HttpMethod.GET, "/requests/**").permitAll() // 요청 조회
                        .requestMatchers(HttpMethod.GET, "/write/requests").authenticated() // 요청 권한 확인
                        .requestMatchers(HttpMethod.POST, "/requests/**").authenticated() // 요청 생성
                        .requestMatchers(HttpMethod.PUT, "/requests/**").authenticated() // 요청 수정
                        .requestMatchers(HttpMethod.DELETE, "/requests/**").authenticated() // 요청 삭제

                        // ===== 기부 관련 엔드포인트 =====
                        .requestMatchers(HttpMethod.POST, "/donate/**").authenticated() // 기부 생성
                        .requestMatchers(HttpMethod.PUT, "/donate/cancel/**").authenticated() // 기부 취소
                        .requestMatchers(HttpMethod.GET, "/donations/top").permitAll() // 상위 5명의 기부자 조회
                        .requestMatchers(HttpMethod.GET, "/test/confirm/**").authenticated()

                        // ===== 대시보드 엔드포인트 =====
                        .requestMatchers(HttpMethod.GET, "/dashboard").authenticated() // 대시보드 데이터 (보호됨)

                        // ===== 공지사항 관련 엔드포인트 =====
                        .requestMatchers(HttpMethod.POST, "/notices", "/notices/{noticeId}/files").hasRole("ADMIN") // 공지사항 생성 및 파일 추가
                        .requestMatchers(HttpMethod.PUT, "/notices/{noticeId}").hasRole("ADMIN") // 공지사항 수정
                        .requestMatchers(HttpMethod.DELETE, "/notices/{noticeId}", "/notices/{noticeId}/files/{fileId}").hasRole("ADMIN") // 공지사항 삭제 및 파일 삭제
                        .requestMatchers(HttpMethod.GET, "/notices/{noticeId}/files/{fileId}").permitAll() // 공지사항 파일 다운로드

                        // ===== 관리자 관련 엔드포인트 =====
                        .requestMatchers("/admin/**").permitAll() // 관리자 페이지

                        // ===== 기타 =====
                        .anyRequest().authenticated() // 그 외의 모든 요청은 인증 필요
                )
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new CustomLogoutFilter(jwtUtil, refreshTokenService), JWTFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())); // CORS 설정

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "https://react-practice-7f2j.vercel.app")); // 허용할 Origin 추가
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "*")); // 허용할 헤더
        configuration.setExposedHeaders(List.of("access", "Content-Type")); // 노출할 헤더
        configuration.setAllowCredentials(true); // 쿠키 사용 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
