package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.entities.PortfolioValue;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface PortfolioValueService {
    PortfolioValue createPortfolioValue(PortfolioValue portfolioValue);
    PortfolioValue getPortfolioValueById(Long id) throws EntityNotFoundException;
    PortfolioValue updatePortfolioValue(Long id, PortfolioValue portfolioValue) throws EntityNotFoundException;
    void deletePortfolioValue(Long id) throws EntityNotFoundException;
    List<PortfolioValue> getPortfolioValuesByPortfolioId(Long portfolioId);
    List<PortfolioValue> getPortfolioValuesByDateRange(Long portfolioId, LocalDateTime startDate, LocalDateTime endDate);
}
