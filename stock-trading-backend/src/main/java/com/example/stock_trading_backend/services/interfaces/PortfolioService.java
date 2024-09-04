package com.example.stock_trading_backend.services.interfaces;

import com.example.stock_trading_backend.entities.Portfolio;
import com.example.stock_trading_backend.exceptions.EntityNotFoundException;

public interface PortfolioService {
    Portfolio createPortfolio(Portfolio portfolio);
    Portfolio getPortfolioById(Long id) throws EntityNotFoundException;
    Portfolio updatePortfolio(Long id, Portfolio portfolio) throws EntityNotFoundException;
    void deletePortfolio(Long id) throws EntityNotFoundException;
    public double getCurrentPortfolioWorth(Long id) throws EntityNotFoundException;
}
