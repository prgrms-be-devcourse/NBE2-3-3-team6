package com.redbox.domain.user.service;

import com.redbox.domain.auth.dto.CustomUserDetails;
import com.redbox.domain.donation.repository.DonationGroupRepository;
import com.redbox.domain.request.dto.ListResponse;
import com.redbox.domain.request.dto.RequestFilter;
import com.redbox.domain.request.entity.Request;
import com.redbox.domain.request.repository.RequestRepository;
import com.redbox.domain.user.dto.*;
import com.redbox.domain.user.entity.User;
import com.redbox.domain.user.exception.DuplicateEmailException;
import com.redbox.domain.user.exception.EmailNotVerifiedException;
import com.redbox.domain.user.exception.PasswordNotMatchException;
import com.redbox.domain.user.exception.UserNotFoundException;
import com.redbox.domain.user.exception.PasswordMismatchException;
import com.redbox.domain.user.repository.EmailVerificationCodeRepository;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.global.entity.PageResponse;
import com.redbox.global.util.RandomCodeGenerator;
import com.redbox.global.util.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class UserService {

    private final SpringTemplateEngine templateEngine;
    private final EmailSender emailSender;
    private final EmailVerificationCodeRepository emailVerificationCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final DonationGroupRepository donationGroupRepository;
    private final RequestRepository requestRepository;

    // 현재 로그인한 사용자의 전체 정보 조회
    public User getCurrentUser() {
        CustomUserDetails userDetails = getCustomUserDetails();
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(UserNotFoundException::new);
    }

    // 현재 로그인한 user_id
    public Long getCurrentUserId() {
        return getCustomUserDetails().getUserId();
    }

    private CustomUserDetails getCustomUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private String createEmailContent(String templateName, String variableName, String variableValue) {
        Context context = new Context();
        context.setVariable(variableName, variableValue);
        return templateEngine.process(templateName, context);
    }

    public void sendVerificationCode(VerificationCodeRequest request) {
        // 이미 회원가입이 된 이메일인지 확인
        if (isDuplicatedEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }
        String verificationCode = RandomCodeGenerator.generateRandomCode();
        String subject = "[Redbox] 이메일 인증 코드입니다.";
        String content = createEmailContent("verification-code-email", "verificationCode", verificationCode);
        emailSender.sendMail(request.getEmail(), subject, content);
        emailVerificationCodeRepository.save(request.getEmail(), verificationCode);
    }

    private boolean isDuplicatedEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional
    public boolean validateVerificationCode(ValidateVerificationCodeRequest request) {
        return emailVerificationCodeRepository.getVerificationCodeByEmail(request.getEmail())
                .filter(verificationCode -> verificationCode.equals(request.getVerificationCode()))
                .map(verificationCode -> {
                    emailVerificationCodeRepository.deleteByEmail(request.getEmail());
                    return Boolean.TRUE;
                })
                .orElse(Boolean.FALSE);
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 이메일 인증이 완료되었는지 확인
        if (!request.isVerified()) {
            throw new EmailNotVerifiedException();
        }

        // 이미 회원가입이 된 이메일인지 확인
        if (isDuplicatedEmail(request.getEmail())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = encodePassword(request.getPassword());
        User user = SignupRequest.toEntity(request, encodedPassword);
        // 처음 회원가입 시 인증된 상태가 아니므로 직접 설정
        user.setCreatedBy(request.getEmail());
        user.setUpdatedBy(request.getEmail());

        userRepository.save(user);
        return new SignupResponse(user.getEmail(), user.getName());
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 사용자 조회
        User user = userRepository.findByEmailAndName(request.getEmail(), request.getUsername())
                .orElseThrow(UserNotFoundException::new);

        // 임시 비밀번호 생성
        String tempPassword = RandomCodeGenerator.generateRandomCode();
        String encodedPassword = encodePassword(tempPassword);

        // 비밀번호 변경
        user.changePassword(encodedPassword);
        userRepository.save(user);

        // 이메일 전송
        String subject = "[Redbox] 임시 비밀번호 안내";
        String content = createEmailContent("temp-password-email", "tempPassword", tempPassword);
        emailSender.sendMail(request.getEmail(), subject, content);
    }

    @Transactional
    public FindIdResponse findUserId(FindIdRequest request) {
        String name = request.getUserName();
        String phoneNumber = request.getPhoneNumber();

        // 해당 정보로 사용자를 찾고, 없으면 예외 던짐
        String email = userRepository.findByNameAndPhoneNumber(name, phoneNumber)
                .orElseThrow(UserNotFoundException::new)
                .getEmail();

        return new FindIdResponse(email);
    }
  
    public UserInfoResponse getUserInfo() {
        User user = getCurrentUser();
        return new UserInfoResponse(user);
    }

    @Transactional
    public UserInfoResponse updateUserInfo(UpdateUserInfoRequest updateRequest) {
        User user = getCurrentUser();

        if (updateRequest.getName() != null) {
            user.changeName(updateRequest.getName());
        }
        if (updateRequest.getPhoneNumber() != null) {
            user.changePhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getRoadAddress() != null) {
            user.changeRoadAddress(updateRequest.getRoadAddress());
        }
        if (updateRequest.getExtraAddress() != null) {
            user.changeExtraAddress(updateRequest.getExtraAddress());
        }
        if (updateRequest.getDetailAddress() != null) {
            user.changeDetailAddress(updateRequest.getDetailAddress());
        }
      
        userRepository.save(user);
        return new UserInfoResponse(user);
    }

    // 비밀 번호 일치 여부 확인 로직
    @Transactional
    public void dropUser(DropInfoRequest request) {

        // 현재 로그인한 사용자 조회
        User currentUser = getCurrentUser();

        // 입력받은 비밀번호와 현재 사용자의 비밀번호 비교
        if (!passwordEncoder.matches(request.getPassword(), currentUser.getPassword())) {
            throw new PasswordMismatchException();
        }

        currentUser.inactive();
        userRepository.save(currentUser);
    }
  
    @Transactional
    public void changePassword(UpdatePasswordRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordNotMatchException();
        }

        User user = getCurrentUser();
        user.changePassword(encodePassword(request.getPassword()));
    }

    public CheckUserResponse checkUser(CheckUserRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                                         .orElseThrow(UserNotFoundException::new);

        return new CheckUserResponse(user.getId(), user.getName());
    }
  
    public PageResponse<DonationResponse> getDonations(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return new PageResponse<>(donationGroupRepository.findAllWithReceiverNameByDonorId(getCurrentUserId(), pageable));
    }

    public PageResponse<ReceptionResponse> getReceptions(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return new PageResponse<>(donationGroupRepository.findAllWithDonorNameByReceiverId(getCurrentUserId(), pageable));
    }

    public PageResponse<ListResponse> getRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page -1, size, Sort.by("createdAt").descending());

        Page<Request> boardPage = requestRepository.findAllByUserIdAndNotDropStatus(getCurrentUserId(), pageable);
        Page<ListResponse> responsePage = boardPage.map(ListResponse::new);

        return new PageResponse<>(responsePage);
    }
}
