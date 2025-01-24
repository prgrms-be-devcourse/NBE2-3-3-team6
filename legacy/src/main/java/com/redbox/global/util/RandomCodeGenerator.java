package com.redbox.global.util;

import java.security.SecureRandom;

public class RandomCodeGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int CODE_LENGTH = 8;

    public static String generateRandomCode() {
        StringBuilder randomCode = new StringBuilder();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(CHARACTERS.length());
            randomCode.append(CHARACTERS.charAt(index));
        }

        return randomCode.toString();
    }
}
