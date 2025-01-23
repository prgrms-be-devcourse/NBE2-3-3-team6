package com.redbox.domain.redcard.service;

import com.redbox.domain.redcard.dto.RegisterRedcardRequest;
import com.redbox.domain.redcard.entity.OwnerType;
import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException;
import com.redbox.domain.redcard.exception.PendingRedcardException;
import com.redbox.domain.redcard.exception.RedcardNotBelongException;
import com.redbox.domain.redcard.exception.RedcardNotFoundException;
import com.redbox.domain.redcard.repository.RedcardRepository;
import com.redbox.domain.user.dto.RedcardResponse;
import com.redbox.domain.user.dto.UpdateRedcardStatusRequest;
import com.redbox.domain.user.repository.UserRepository;
import com.redbox.domain.user.service.UserService;
import com.redbox.global.entity.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedcardService {

    private final UserRepository userRepository;
    private final RedcardRepository redcardRepository;
    private final UserService userService;

    @Transactional
    public void registerRedCard(RegisterRedcardRequest request){
        boolean isDuplicate = redcardRepository.findAll().stream().map(Redcard::getSerialNumber)
                .anyMatch(serialNumber -> serialNumber.equals(request.getCardNumber()));

        if (isDuplicate) {
            throw new DuplicateSerialNumberException();
        }

        // Redcard 생성
        Redcard redcard = Redcard.builder()
                .userId(userService.getCurrentUserId())
                .donationDate(request.getDonationDate())
                .serialNumber(request.getCardNumber())
                .hospitalName(request.getHospitalName())
                .redcardStatus(RedcardStatus.AVAILABLE)
                .ownerType(OwnerType.USER)
                .build();

        redcardRepository.save(redcard);
    }

    public PageResponse<RedcardResponse> getRedcards(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Redcard> redcards = redcardRepository.findAllByUserId(userService.getCurrentUserId(), pageable);
        return new PageResponse<>(redcards.map(RedcardResponse::new));
    }

    public void updateRedCardUser(long redcardId, long receiverId) {
        Redcard redcard = getRedcardById(redcardId);
        redcard.updateUser(receiverId);
        redcard.changeRedcardStatus(RedcardStatus.AVAILABLE);
    }

    public Redcard getRedcardById(long redcardId) {
        Redcard redcard = redcardRepository.findById(redcardId).orElse(null);

        if (redcard == null) {
            throw new RedcardNotFoundException();
        }
        return redcard;
    }

    @Transactional
    public void updateRedcardStatus(UpdateRedcardStatusRequest request, Long redcardId) {
        Redcard redcard = redcardRepository.findByUserIdAndId(userService.getCurrentUserId(), redcardId)
                .orElseThrow(RedcardNotBelongException::new);
        if (redcard.getRedcardStatus().equals(RedcardStatus.PENDING)) {
            throw new PendingRedcardException();
        }
        redcard.changeRedcardStatus(request.validateAndGetOppositeStatus());
    }

    public void updateRedCardList(List<Redcard> redcardList, Long receiveUserId, OwnerType ownerType) {
        for (Redcard redcard : redcardList) {
            redcard.updateUser(receiveUserId);
            redcard.changeOwnerType(ownerType);
        }
    }

    public void updateRedCardStatusPending(List<Redcard> redcardList) {
        for (Redcard redcard : redcardList) {
            redcard.changeRedcardStatus(RedcardStatus.PENDING);
        }
    }

    public void updateRedCardCancel(long redcardId) {
        Redcard redcard = getRedcardById(redcardId);
        redcard.changeRedcardStatus(RedcardStatus.AVAILABLE);
    }
}
