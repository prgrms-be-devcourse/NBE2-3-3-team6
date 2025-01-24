package com.redbox.global.util

import java.security.SecureRandom

class RandomCodeGenerator {
    companion object {
        private const val CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789"
        private val SECURE_RANDOM = SecureRandom()
        private const val CODE_LENGTH = 8

        fun generateRandomCode(): String {
            val randomCode = StringBuilder()

            for (i in 0 until CODE_LENGTH) {
                val index = SECURE_RANDOM.nextInt(CHARACTERS.length)
                randomCode.append(CHARACTERS[index])
            }

            return randomCode.toString()
        }
    }
}