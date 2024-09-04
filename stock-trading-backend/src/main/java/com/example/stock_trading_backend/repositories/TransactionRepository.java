package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByPortfolioId(Long portfolioId);
    List<Transaction> findByCryptoId(Long cryptoId);
}
