package com.concurrency_issue_practice.stock.repository;

import com.concurrency_issue_practice.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}
