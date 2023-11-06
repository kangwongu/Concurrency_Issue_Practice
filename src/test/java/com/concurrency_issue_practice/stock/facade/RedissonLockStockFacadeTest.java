package com.concurrency_issue_practice.stock.facade;

import com.concurrency_issue_practice.stock.domain.Stock;
import com.concurrency_issue_practice.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedissonLockStockFacadeTest {

    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void before() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    void after() {
        stockRepository.deleteAll();
    }

    @Test
    public void 재고감소() throws InterruptedException {
        // given
        // when
        redissonLockStockFacade.decrease(1L, 1L);

        // then
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(99);
    }

    @Test
    public void 동시에_100개의_요청() throws InterruptedException {
        // given
        int threadCount = 100;
        // 멀티스레드, 비동기로 실행하는 작업을 단순화해서 사용할 수 있게 도와줌
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        // 100개의 요청이 끝날 때까지 기다림
        CountDownLatch latch = new CountDownLatch(threadCount);     // 다른 쓰레드에서 수행중인 작업이 완료될 때까지 대기할 수 있도록 도와줌

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(0);
    }
}