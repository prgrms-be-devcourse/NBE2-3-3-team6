package com.redbox.domain.user.service;

import com.redbox.domain.auth.dto.CustomUserDetails;
import com.redbox.domain.user.dto.UpdatePasswordRequest;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.exception.PasswordNotMatchException;
import com.redbox.domain.user.repository.EmailVerificationCodeRepository;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.global.exception.BusinessException;
import com.redbox.global.util.email.EmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    // 실제 사용하지 않는 의존성들은 Mock으로 대체
    @MockitoBean
    private EmailSender emailSender;

    @MockitoBean
    private SpringTemplateEngine templateEngine;

    @MockitoBean
    private EmailVerificationCodeRepository emailVerificationCodeRepository;

    private User setUserAndSecurityContext(String email, String password, String name) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
        userRepository.save(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        return user;
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @DisplayName("로그인한 사용자의 비밀번호를 성공적으로 변경한다.")
    @Test
    void changePasswordSuccessful() throws Exception {
        //given
        User user = setUserAndSecurityContext("test@test.com", "a123456!", "테스터");
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .password("aa123123@")
                .passwordConfirm("aa123123@")
                .build();

        // when
        userService.changePassword(request);

        // then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(passwordEncoder.matches(request.getPassword(), updatedUser.getPassword())).isTrue();
    }

    @DisplayName("비밀번호와 비밀번호가 일치하지 않은 경우 예외가 발생한다.")
    @Test
    void PasswordDoesNotMatchPasswordConfirmThrowsException() throws Exception {
        //given
        User user = setUserAndSecurityContext("test@test.com", "a123456!", "테스터");
        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .password("aa123123@")
                .passwordConfirm("aa123456!!!")
                .build();

        //when & then
        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(PasswordNotMatchException.class)
                .hasMessage("비밀번호와 비밀번호 확인이 일치하지 않습니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}