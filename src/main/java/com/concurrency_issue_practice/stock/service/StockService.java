package com.concurrency_issue_practice.stock.service;

import com.concurrency_issue_practice.stock.annotation.DistributionLock;
import com.concurrency_issue_practice.stock.domain.Stock;
import com.concurrency_issue_practice.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

//    @Transactional
    @DistributionLock(key = "#id")
    public synchronized void decrease(Long id, Long quantity) {
        /*
        1. Stock 조회
        2. 조회한 Stock 재고 감소
        3. 갱신된 재고 값 저장
         */
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
