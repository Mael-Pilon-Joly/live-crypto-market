package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.PortfolioCrypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioCryptoRepository extends JpaRepository<PortfolioCrypto, Long> {
    List<PortfolioCrypto> findByPortfolioId(Long portfolioId);
}
