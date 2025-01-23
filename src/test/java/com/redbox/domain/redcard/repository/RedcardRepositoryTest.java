package com.redbox.domain.redcard.repository;

import com.redbox.domain.redcard.entity.Redcard;
import com.redbox.domain.redcard.entity.RedcardStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class RedcardRepositoryTest {

    @Test
    @DisplayName("레드카드 생성 테스트")
    @Transactional
    void createRedCardTest() {
        //Given
        Long userId = 1L;
        LocalDate donationDate = LocalDate.of(2024, 12, 25);
        String serialNumber = "12";
        RedcardStatus status = RedcardStatus.AVAILABLE;

        //When
        Redcard cardData = Redcard.builder()
                .userId(userId)
                .donationDate(donationDate)
                .serialNumber(serialNumber)
                .redcardStatus(status)
                .build();

        // Then
        assertEquals(userId, cardData.getUserId());
        assertEquals(donationDate, cardData.getDonationDate());
        assertEquals(serialNumber, cardData.getSerialNumber());
        assertEquals(status, cardData.getRedcardStatus());
    }
}