package com.concurrency_issue_practice.stock.service;

import com.concurrency_issue_practice.stock.domain.Stock;
import com.concurrency_issue_practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OptimisticLockStockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.getByIdWithOptimisticLock(id);
        stock.decrease(quantity);

        stockRepository.save(stock);
    }
}
