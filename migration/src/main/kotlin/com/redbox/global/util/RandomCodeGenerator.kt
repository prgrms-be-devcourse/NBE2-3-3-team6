package com.redbox.global.util

import java.security.SecureRandom

private const val CHARACTERS: String = "abcdefghijklmnopqrstuvwxyz0123456789"
private val SECURE_RANDOM: SecureRandom = SecureRandom()
private const val CODE_LENGTH: Int = 8

class RandomCodeGenerator {

    companion object {
        fun generateRandomCode(): String {
            val randomCode: StringBuilder = StringBuilder()

            for (i in 0 until CODE_LENGTH) {
                val index = SECURE_RANDOM.nextInt(CHARACTERS.length)
                randomCode.append(CHARACTERS[index])
            }

            return randomCode.toString()
        }
    }
}