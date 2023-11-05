package com.concurrency_issue_practice.stock.facade;

import com.concurrency_issue_practice.stock.service.OptimisticLockStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Update 실패 시, 수행할 로직까지 구현하기 위해 Facade 패턴으로 구현 (재고감소로직 + 재고업데이트실패시, 처리로직)
@Component
@RequiredArgsConstructor
public class OptimisticLockStockFacade {

    private final OptimisticLockStockService optimisticLockStockService;

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while (true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch (Exception e) {
                Thread.sleep(50);
            }
        }
    }
}
