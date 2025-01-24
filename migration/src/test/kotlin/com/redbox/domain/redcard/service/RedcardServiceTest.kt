package com.redbox.domain.redcard.service

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.exception.DuplicateSerialNumberException
import com.redbox.domain.redcard.repository.RedcardRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*

class RedcardServiceTest {

    private lateinit var redcardRepository: RedcardRepository
    private lateinit var redcardService: RedcardService
    private val logger: Logger = LoggerFactory.getLogger(RedcardServiceTest::class.java)

    @BeforeEach
    fun setUp() {
        redcardRepository = mockk()
        redcardService = RedcardService(redcardRepository)
    }

    @Test
    fun `should register redcard successfully`() {
        // Given
        val request = createRegisterRequest()
        val redcard = createRedcard(request)

        every { redcardRepository.findBySerialNumber(request.cardNumber) } returns Optional.empty()
        every { redcardRepository.save(any()) } answers { firstArg() }

        // When
        redcardService.registerRedCard(request)

        // Then
        verify { redcardRepository.save(withArg { it.serialNumber == request.cardNumber }) }

        // Log registered Redcard
        logger.info("Successfully registered Redcard: $redcard")
    }

    @Test
    fun `should throw exception when duplicate serial number exists`() {
        // Given
        val request = createRegisterRequest()

        every { redcardRepository.findBySerialNumber(request.cardNumber) } returns Optional.of(mockk())

        // When & Then
        assertThrows<DuplicateSerialNumberException> {
            redcardService.registerRedCard(request)
        }

        verify { redcardRepository.findBySerialNumber(request.cardNumber) }
    }

    // Helper Methods
    private fun createRegisterRequest() = RegisterRedcardRequest(
        cardNumber = "1234-5678",
        donationDate = LocalDate.now(),
        hospitalName = "Test Hospital"
    )

    private fun createRedcard(request: RegisterRedcardRequest) = Redcard(
        userId = 0L,
        donationDate = request.donationDate,
        serialNumber = request.cardNumber,
        hospitalName = request.hospitalName,
        redcardStatus = RedcardStatus.AVAILABLE,
        ownerType = OwnerType.USER
    )
}
