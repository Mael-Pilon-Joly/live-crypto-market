package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioRepository  extends JpaRepository<Portfolio, Long> {
}
