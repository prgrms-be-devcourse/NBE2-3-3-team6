package com.redbox.domain.community.attach.strategy

import com.redbox.domain.community.attach.entity.Category
import org.springframework.stereotype.Component

@Component
class FileAttachStrategyFactory(strategyList: List<FileAttachStrategy>) {

    // 전략을 저장하는 맵, Category를 키로 사용
    private val strategies: Map<Category, FileAttachStrategy> = mapOf(
        Category.FUNDING to findStrategy(strategyList, FundingFileStrategy::class.java),
        Category.NOTICE to findStrategy(strategyList, NoticeFileStrategy::class.java)
    )

    // 전략 리스트에서 특정 타입의 전략을 찾는 private 메서드
    private fun findStrategy(
        strategies: List<FileAttachStrategy>,
        strategyClass: Class<out FileAttachStrategy>
    ): FileAttachStrategy {
        return strategies.find { strategyClass.isInstance(it) }
            ?: throw IllegalStateException("해당 전략이 설정되지 않았습니다: ${strategyClass.simpleName}")
    }

    // 카테고리에 맞는 전략을 반환하는 메서드
    fun getStrategy(category: Category): FileAttachStrategy? {
        return strategies[category]
    }
}
