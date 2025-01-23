package com.redbox.domain.attach.strategy;

import com.redbox.domain.attach.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class FileAttachStrategyFactory {

    private final Map<Category, FileAttachStrategy> strategies;

    public FileAttachStrategyFactory(List<FileAttachStrategy> strategyList) {
        strategies = Map.of(
                Category.NOTICE, findStrategy(strategyList, NoticeFileStrategy.class),
                Category.FUNDING, findStrategy(strategyList, RequestFileStrategy.class)
        );
    }

    // 전략 리스트에서 특정 타입의 전략을 찾는 private 메서드
    private FileAttachStrategy findStrategy(List<FileAttachStrategy> strategies,
                                            Class<? extends FileAttachStrategy> strategyClass) {
        return strategies.stream()
                .filter(strategyClass::isInstance)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 전략이 설정되지 않았습니다: " + strategyClass.getSimpleName()));
    }

    public FileAttachStrategy getStrategy(Category category) {
        return strategies.get(category);
    }
}
