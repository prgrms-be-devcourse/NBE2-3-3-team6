package com.redbox.domain.redcard.service

import com.redbox.domain.redcard.dto.RegisterRedcardRequest
import com.redbox.domain.redcard.entity.OwnerType
import com.redbox.domain.redcard.entity.Redcard
import com.redbox.domain.redcard.entity.RedcardStatus
import com.redbox.domain.redcard.repository.RedcardRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
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
        println("저장된 Redcard: $savedRedcard")
    }

    @Test
    fun `유저 아이디가 0인 헌혈증 목록 조회`() {
        // Given: 기존 데이터베이스에 이미 유저 ID가 0인 헌혈증들이 저장되어 있음
        val pageRequest = PageRequest.of(0, 5) // 페이지 크기 3

        // When: 유저 ID가 0인 헌혈증 조회
        val result = redcardService.getRedcards(1, 5) // page=1 (첫 페이지), size=3

        // Then: 반환된 헌혈증들이 모두 userId = 0인지 확인
        assert(result.content.isNotEmpty()) { "조회된 헌혈증이 없습니다." }
        assert(result.content.all { it.userId == 0L }) { "조회된 헌혈증 중 userId가 0이 아닌 데이터가 포함되어 있습니다." }

        println("유저 ID가 0인 헌혈증 목록:")
        result.content.forEach { println(it) } // 각 헌혈증 정보를 개별 출력
    }

}
