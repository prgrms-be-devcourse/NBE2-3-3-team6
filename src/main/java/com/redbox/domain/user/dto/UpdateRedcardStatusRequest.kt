package com.redbox.domain.user.dto;

import com.redbox.domain.redcard.entity.RedcardStatus;
import com.redbox.domain.redcard.exception.InvalidRedcardStatusException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRedcardStatusRequest {
    private static final String STATUS_AVAILABLE = "available";
    private static final String STATUS_USED = "used";

    @NotNull
    private String cardStatus;

    public RedcardStatus validateAndGetOppositeStatus() {
        if (cardStatus == null || !(cardStatus.equals(STATUS_AVAILABLE) || cardStatus.equals(STATUS_USED))) {
            throw new InvalidRedcardStatusException();
        }

        return oppositeRedcardStatus();
    }

    public RedcardStatus oppositeRedcardStatus() {
        return cardStatus.equals(STATUS_AVAILABLE) ? RedcardStatus.USED : RedcardStatus.AVAILABLE;
    }
}
