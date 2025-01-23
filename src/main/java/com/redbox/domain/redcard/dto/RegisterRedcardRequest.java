package com.redbox.domain.redcard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRedcardRequest {

    @NotBlank(message = "헌혈증 번호를 입력해주세요.")
    private String cardNumber;

    @NotNull(message = "헌혈일자를 입력해주세요.")
    private LocalDate donationDate;

    @NotBlank(message = "헌혈장소를 입력해주세요.")
    private String hospitalName;
}
