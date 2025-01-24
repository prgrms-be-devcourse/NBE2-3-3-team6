package com.redbox.domain.redcard.service;

import com.redbox.domain.auth.dto.CustomUserDetails;
import com.redbox.domain.redcard.dto.RegisterRedcardRequest;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException;
import com.redbox.domain.redcard.exception.PendingRedcardException;
import com.redbox.domain.redcard.exception.RedcardNotBelongException;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.user.dto.RedcardResponse;
import com.redbox.domain.user.dto.UpdateRedcardStatusRequest;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import com.redbox.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RedcardServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedcardRepository redcardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedcardService redcardService;

    private User setUserAndSecurityContext(String email) {
        User user = User.builder()
                .email(email)
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

    @DisplayName("로그인한 사용자가 헌혈증 등록을 성공한다.")
    @Test
    void registerRedcardSuccessful() throws Exception {
        //given
        User user = setUserAndSecurityContext("test@test.com");
        RegisterRedcardRequest request = RegisterRedcardRequest.builder()
                .cardNumber("00-00-000000")
                .donationDate(LocalDate.of(2024, 12, 1))
                .hospitalName("헌혈의집 충북대센터")
                .build();

        //when
        redcardService.registerRedCard(request);

        //then
        Redcard savedRedcard = redcardRepository.findBySerialNumber(request.getCardNumber())
                .orElseThrow();

        assertThat(savedRedcard.getUserId()).isEqualTo(user.getId());
        assertThat(savedRedcard.getSerialNumber()).isEqualTo(request.getCardNumber());
        assertThat(savedRedcard.getDonationDate()).isEqualTo(request.getDonationDate());
        assertThat(savedRedcard.getHospitalName()).isEqualTo(request.getHospitalName());
        assertThat(savedRedcard.getRedcardStatus()).isEqualTo(RedcardStatus.AVAILABLE);
    }

    @DisplayName("헌혈증 등록시 이미 등록된 헌혈증 번호이면 예외가 발생한다.")
    @Test
    void registerRedcardWithDuplicateSerialNumberThrowException() throws Exception {
        //given
        User user = setUserAndSecurityContext("test@test.com");
        RegisterRedcardRequest request = RegisterRedcardRequest.builder()
                .cardNumber("00-00-000000")
                .donationDate(LocalDate.of(2024, 12, 1))
                .hospitalName("헌혈의집 충북대센터")
                .build();
        redcardService.registerRedCard(request);

        RegisterRedcardRequest duplicateRequest = RegisterRedcardRequest.builder()
                .cardNumber("00-00-000000")
                .donationDate(LocalDate.of(2024, 12, 1))
                .hospitalName("헌혈의집 충북대센터")
                .build();

        //when & then
        assertThatThrownBy(() -> redcardService.registerRedCard(duplicateRequest))
                .isInstanceOf(DuplicateSerialNumberException.class)
                .hasMessage("이미 등록된 헌혈증입니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("로그인한 사용자의 헌혈증 목록을 페이징하여 조회한다.")
    @Test
    void getRedcardsWithPagination() {
        // given
        User user = setUserAndSecurityContext("test@test.com");

        // 테스트용 헌혈증 데이터 생성
        for(int i = 1; i <= 15; i++) {  // 15개의 테스트 데이터
            Redcard redcard = Redcard.builder()
                    .userId(user.getId())
                    .serialNumber("test-" + i)
                    .donationDate(LocalDate.now())
                    .hospitalName("병원" + i)
                    .redcardStatus(RedcardStatus.AVAILABLE)
                    .build();
            redcardRepository.save(redcard);
        }

        // when
        PageResponse<RedcardResponse> response = redcardService.getRedcards(1, 6);  // 첫 페이지, 10개씩

        // then
        assertThat(response.getContent()).hasSize(6);  // 첫 페이지에 6개
        assertThat(response.getTotalElements()).isEqualTo(15);  // 전체 15개
        assertThat(response.getTotalPages()).isEqualTo(3);  // 총 3페이지
    }

    @DisplayName("헌혈증 목록의 마지막 페이지를 조회한다.")
    @Test
    void getRedcardsLastPage() {
        // given
        User user = setUserAndSecurityContext("test@test.com");

        for(int i = 1; i <= 15; i++) {
            Redcard redcard = Redcard.builder()
                    .userId(user.getId())
                    .serialNumber("test-" + i)
                    .donationDate(LocalDate.now())
                    .hospitalName("병원" + i)
                    .redcardStatus(RedcardStatus.AVAILABLE)
                    .build();
            redcardRepository.save(redcard);
        }

        // when
        PageResponse<RedcardResponse> response = redcardService.getRedcards(3, 6);  // 두번째 페이지

        // then
        assertThat(response.getContent()).hasSize(3);  // 마지막 페이지에 3개
        assertThat(response.getTotalElements()).isEqualTo(15);
    }

    @DisplayName("헌혈증 데이터가 없을 때 빈 목록을 반환한다.")
    @Test
    void getRedcardsEmptyList() {
        // given
        User user = setUserAndSecurityContext("test@test.com");

        // when
        PageResponse<RedcardResponse> response = redcardService.getRedcards(1, 6);

        // then
        assertThat(response.getContent()).isEmpty();
        assertThat(response.getTotalElements()).isZero();
        assertThat(response.getTotalPages()).isZero();
    }

    @DisplayName("기부 진행중인 헌혈증은 상태를 변경할 때 예외가 발생한다.")
    @Test
    void changeRedcardStatusFromPendingToAnotherStatusThrowException() throws Exception {
        //given
        User user = setUserAndSecurityContext("test@test.com");
        Redcard redcard = Redcard.builder()
                .userId(user.getId())
                .serialNumber("test-1")
                .donationDate(LocalDate.now())
                .hospitalName("테스트병원")
                .redcardStatus(RedcardStatus.PENDING)  // PENDING 상태로 설정
                .build();
        redcardRepository.save(redcard);

        UpdateRedcardStatusRequest request = new UpdateRedcardStatusRequest("available");

        //when & then
        assertThatThrownBy(() -> redcardService.updateRedcardStatus(request, redcard.getId()))
                .isInstanceOf(PendingRedcardException.class)
                .hasMessage("기부 진행중인 헌혈증입니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @DisplayName("자신의 것이 아닌 헌혈증의 상태를 변경할 때 예외가 발생한다.")
    @Test
    void updateRedcardStatusWithNotOwnedRedcardThrowException() {
        //given
        User owner = User.builder()
                .email("owner@test.com")
                .build();
        userRepository.save(owner);

        // 다른 사용자의 헌혈증 생성
        Redcard redcard = Redcard.builder()
                .userId(owner.getId())
                .serialNumber("test-1")
                .donationDate(LocalDate.now())
                .hospitalName("테스트병원")
                .redcardStatus(RedcardStatus.AVAILABLE)
                .build();
        redcardRepository.save(redcard);

        // 현재 로그인한 사용자를 다른 사용자로 설정
        setUserAndSecurityContext("another@test.com");

        UpdateRedcardStatusRequest request = new UpdateRedcardStatusRequest("used");

        //when & then
        assertThatThrownBy(() -> redcardService.updateRedcardStatus(request, redcard.getId()))
                .isInstanceOf(RedcardNotBelongException.class)
                .hasMessage("자신이 소유한 헌혈증이 아닙니다.")
                .asInstanceOf(type(BusinessException.class))
                .extracting(ex -> ex.getErrorCodes().getStatus())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }
}