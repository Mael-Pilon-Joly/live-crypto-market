package com.example.stock_trading_backend.repositories;

import com.example.stock_trading_backend.entities.PortfolioValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PortfolioValueRepository extends JpaRepository<PortfolioValue, Long> {
    List<PortfolioValue> findByPortfolioId(Long id);
    List<PortfolioValue> findByPortfolioIdAndDateBetween(Long portfolioId, LocalDateTime startDate, LocalDateTime endDate);

}
