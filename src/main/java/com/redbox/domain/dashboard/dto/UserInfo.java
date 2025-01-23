package com.redbox.domain.dashboard.dto;

import com.redbox.domain.user.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserInfo {
    private String name;
    private LocalDate birth;
    private Gender gender;
    private String phoneNumber;
}
