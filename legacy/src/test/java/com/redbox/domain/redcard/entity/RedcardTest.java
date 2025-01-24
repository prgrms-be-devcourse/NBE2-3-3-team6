package com.redbox.domain.redcard.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RedcardTest {

    @DisplayName("헌혈증의 상태를 사용가능에서 사용완료로 바꾼다.")
    @Test
    void updateRedcardStatusShouldChangeStatusFromAvailableToUsed() throws Exception {
        //given
        Redcard redcard = Redcard.builder()
                .serialNumber("00-00-000000")
                .donationDate(LocalDate.of(2025, 1, 1))
                .hospitalName("헌혈한 병원")
                .redcardStatus(RedcardStatus.AVAILABLE)
                .build();

        //when
        redcard.changeRedcardStatus(RedcardStatus.USED);
        
        //then
        assertThat(redcard.getRedcardStatus()).isEqualTo(RedcardStatus.USED);
    }

    @DisplayName("헌혈증의 상태를 사용완료에서 사용가능으로 바꾼다.")
    @Test
    void updateRedcardStatusShouldChangeStatusFromUsedToAvailable() throws Exception {
        //given
        Redcard redcard = Redcard.builder()
                .serialNumber("00-00-000000")
                .donationDate(LocalDate.of(2025, 1, 1))
                .hospitalName("헌혈한 병원")
                .redcardStatus(RedcardStatus.USED)
                .build();

        //when
        redcard.changeRedcardStatus(RedcardStatus.AVAILABLE);

        //then
        assertThat(redcard.getRedcardStatus()).isEqualTo(RedcardStatus.AVAILABLE);
    }
}