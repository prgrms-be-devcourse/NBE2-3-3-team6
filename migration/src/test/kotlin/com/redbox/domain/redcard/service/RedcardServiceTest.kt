package com.redbox.domain.redcard.service

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.repository.RedcardRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class RedcardServiceTest {

    @Autowired
    private lateinit var redcardService: RedcardService

    @Autowired
    private lateinit var redcardRepository: RedcardRepository

    @Test
    fun `헌혈증 등록 후 DB에 저장`() {
        // Given
        val request = RegisterRedcardRequest(
            cardNumber = "1234-5678",
            donationDate = LocalDate.now(),
            hospitalName = "Test Hospital"
        )

        // When
        redcardService.registerRedCard(request)

        // Then
        val savedRedcard: Redcard? = redcardRepository.findBySerialNumber("1234-5678").orElse(null)
        assertNotNull(savedRedcard)
        println("Saved Redcard: $savedRedcard")
    }
}
