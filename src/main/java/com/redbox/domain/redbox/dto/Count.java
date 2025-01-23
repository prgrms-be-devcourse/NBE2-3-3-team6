package com.redbox.domain.redbox.dto;

import com.redbox.domain.donation.exception.DonationAmountExceededException;
import com.redbox.domain.redbox.exception.NegativeQuantityException;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Count {
    private final int value;

    protected Count() {
        this.value = 0;
    }

    public Count(int count) {
        if (count < 0) {
            throw new NegativeQuantityException();
        }
        this.value = count;
    }

    public Count add(int amount) {
        return new Count(value + amount);
    }

    public Count subtract(int amount) {
        if (this.value < amount) {
            throw new DonationAmountExceededException();
        }
        return new Count(value - amount);
    }
}
